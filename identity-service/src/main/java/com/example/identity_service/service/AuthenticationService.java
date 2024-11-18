package com.example.identity_service.service;

import com.example.event.dto.NotificationEvent;
import com.example.identity_service.dto.identity.Credential;
import com.example.identity_service.dto.identity.UserCreationParam;
import com.example.identity_service.dto.request.*;
import com.example.identity_service.dto.response.*;
import com.example.identity_service.entity.InvalidatedToken;
import com.example.identity_service.entity.RefreshToken;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.exception.ErrorNormalizer;
import com.example.identity_service.mapper.UserMapper;
import com.example.identity_service.repository.httpClient.IdentityClient;
import com.example.identity_service.repository.InvalidatedTokenRepository;
import com.example.identity_service.repository.RefreshTokenRepository;
import com.example.identity_service.repository.UserRepository;
import com.example.identity_service.repository.httpClient.OutboundIdentityClient;
import com.example.identity_service.repository.httpClient.OutboundUserClient;
import com.example.identity_service.repository.httpClient.ProfileClient;
import com.example.identity_service.service.keycloak.KeyCloakService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.Normalizer;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    IdentityClient identityClient;
    ErrorNormalizer errorNormalizer;
    RefreshTokenRepository refreshTokenRepository;
    OutboundUserClient outboundUserClient;
    OutboundIdentityClient outboundIdentityClient;
    UserMapper userMapper;
    KeyCloakService keyCloakService;
    KafkaTemplate<String , Object> kafkaTemplate;
    ProfileClient profileClient;
    @NonFinal
    @Value("${google.clientId}")
    protected String CLIENT_ID;

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;


    @NonFinal
    @Value("${google.clientSecret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${google.redirectUri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public AuthenticationResponse outboundAuthenticate(String code) throws JOSEException, ParseException {
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .grantType(GRANT_TYPE)
                        .redirectUri(REDIRECT_URI)
                        .build());
        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());
        log.info("UserInfo: {}", userInfo);

        boolean isRegister = false;
        if(userRepository.existsByEmail(userInfo.getEmail())) {
            isRegister = true;
        }
        log.info("isRegister: {}", isRegister);
        String userId = null;

        if(!isRegister) {
            var token = identityClient.exchangeToken(ClientExchangeTokenRequest.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());
            var creationResponse = identityClient.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(convert(userInfo.getName()))
                            .email(userInfo.getEmail())
                            .firstName(userInfo.getGivenName())
                            .lastName(userInfo.getFamilyName())
                            .enabled(true)
                            .emailVerified(true)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value("1")
                                    .build()))
                            .build());
            userId = extractUserId(creationResponse);
            userRepository.save(User.builder()
                    .username(convert(userInfo.getName()))
                    .email(userInfo.getEmail())
                    .userId(userId)
                    .build());
        }
        log.info("UserId: {}", userId);
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(() ->
                new AppException(ErrorCode.EMAIL_NOT_EXISTED)
                );
        log.info("User: {}", user);

        UserExchangeTokenRequest TokenRequest = UserExchangeTokenRequest.builder()
                .client_id(clientId)
                .client_secret(clientSecret)
                .grant_type("password")
                .scope("openid")
                .username(user.getUsername())
                .password("1")
                .build();

        UserExchangeTokenResponse token = identityClient.exchangeUserToken(TokenRequest);
        SignedJWT signedJWT =  SignedJWT.parse(token.getAccessToken());
        RefreshToken refreshToken = RefreshToken.builder()
                .id(signedJWT.getJWTClaimsSet().getJWTID())
                .refreshToken(token.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        if(!isRegister) {
            profileClient.createProfile(ProfileCreationRequest.builder()
                            .firstName(userInfo.getGivenName())
                            .lastName(userInfo.getFamilyName())
                            .userId(user.getId())
                    .build());

            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(userInfo.getEmail())
                    .subject(userInfo.getName())
                    .build();

            kafkaTemplate.send("notification-delivery", notificationEvent);
        }

        return AuthenticationResponse.builder()
                .token(token.getAccessToken()).authenticated(true)
                .build();



    }

    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.EMAIL_NOT_EXISTED)
                );
        String userId = user.getUserId();
        keyCloakService.updatePassword(userId);
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().get("Location").getFirst();
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        String token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public RefreshTokenResponse refreshToken() throws JOSEException, ParseException {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .token(getToken())
                .build();
        SignedJWT signedJWT = SignedJWT.parse(request.getToken());
        String id = signedJWT.getJWTClaimsSet().getJWTID();
        RefreshToken old_refreshToken = refreshTokenRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.UNAUTHENTICATED));
        RefreshTokenExchangeRequest TokenRequest = RefreshTokenExchangeRequest.builder()
                .client_id(clientId)
                .client_secret(clientSecret)
                .grant_type("refresh_token")
                .refresh_token(old_refreshToken.getRefreshToken())
                .build();

        RefreshTokenExchangeResponse token = identityClient.refreshToken(TokenRequest);
        signedJWT =  SignedJWT.parse(token.getAccessToken());
        RefreshToken new_refreshToken = RefreshToken.builder()
                .id(signedJWT.getJWTClaimsSet().getJWTID())
                .refreshToken(token.getAccessToken())
                .build();

        refreshTokenRepository.save(new_refreshToken);
        return RefreshTokenResponse.builder()
                .token(token.getAccessToken())
                .authenticated(true)
                .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws JOSEException , ParseException {
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            UserExchangeTokenRequest TokenRequest = UserExchangeTokenRequest.builder()
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .grant_type("password")
                    .scope("openid")
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .build();

            UserExchangeTokenResponse token = identityClient.exchangeUserToken(TokenRequest);
            SignedJWT signedJWT =  SignedJWT.parse(token.getAccessToken());
            RefreshToken refreshToken = RefreshToken.builder()
                    .id(signedJWT.getJWTClaimsSet().getJWTID())
                    .refreshToken(token.getRefreshToken())
                    .build();

            refreshTokenRepository.save(refreshToken);

            return AuthenticationResponse.builder()
                    .token(token.getAccessToken()).authenticated(true)
                    .build();
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }



    public void logout() throws JOSEException, ParseException {
        try {
            String token = getToken();
            SignedJWT signedJWT = verifyToken(token);
            var jit = signedJWT.getJWTClaimsSet().getJWTID();
            InvalidatedToken invalidatedToken = InvalidatedToken
                    .builder().id(jit).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }


    }


    public SignedJWT verifyToken(String token) throws JOSEException , ParseException {


        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expriedTime = new Date(signedJWT.getJWTClaimsSet()
                .getIssueTime().toInstant().toEpochMilli()

        );
        if(expriedTime.after(new Date())) {
            throw  new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;

    }

    private String getToken() throws JOSEException, ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");
        String token = null;

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

        } else {
            throw  new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return token;

    }

    private String convert(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String noDiacritics = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");
        String username = noDiacritics.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
        username = username.replaceAll("_+", "_").replaceAll("^_|_$", "");
        return username;
    }

}

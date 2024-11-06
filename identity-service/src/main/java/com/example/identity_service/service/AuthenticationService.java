package com.example.identity_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.example.event.dto.NotificationEvent;
import com.example.identity_service.constant.PredefinedRole;
import com.example.identity_service.dto.request.*;
import com.example.identity_service.entiy.Role;
import com.example.identity_service.repository.httpClient.OutboundIdentityClient;
import com.example.identity_service.repository.httpClient.OutboundUserClient;
import com.example.identity_service.repository.httpClient.ProfileClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.identity_service.dto.response.AuthenticationResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.dto.response.RefreshTokenResponse;
import com.example.identity_service.entiy.InvalidatedToken;
import com.example.identity_service.entiy.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.repository.InvalidatedTokenRepository;
import com.example.identity_service.repository.UserRepostitory;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepostitory userRepostitory;
    InvalidatedTokenRepository invalidatedTokenRepository;
    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;
    ProfileClient profileClient;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${google.clientId}")
    protected String CLIENT_ID;


    @NonFinal
    @Value("${google.clientSecret}")
    protected String CLIENT_SECRET;


    private String REDIRECT_URI = "https://movie.nhatanhweb.website/authenticate";

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    KafkaTemplate<String, Object> kafkaTemplate;

    public IntrospectResponse introspect(IntroSpectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse outboundAuthenticate(String code) {
        log.info("Login By Google");
        log.info("code: {}", code);
        log.info("ClientID: {}", CLIENT_ID);
        log.info("ClientSecret: {}", CLIENT_SECRET);
        log.info("GRANT_TYPE: {}", GRANT_TYPE);
        log.info("REDIRECTED_URL : {}", REDIRECT_URI);
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());
        log.info("Google Reponse: {}", response);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().name(PredefinedRole.USER_ROLE).build());

        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());
        boolean isRegister = false;
        if (userRepostitory.existsByEmail(userInfo.getEmail())) {
            isRegister = true;
        }
        log.info("UserInfo: {}", userInfo);

        User user = userRepostitory.findByEmail(userInfo.getEmail()).orElseGet(() -> {

                    User newUser = userRepostitory.save(User.builder()
                            .username(userInfo.getName())
                            .email(userInfo.getEmail())
                            .roles(roles)
                            .build());
                    profileClient.createProfile(ProfileCreationRequest.builder()
                            .firstName(userInfo.getGivenName())
                            .lastName(userInfo.getFamilyName())
                            .userId(newUser.getId())
                            .build());
                    return newUser;
                }

        );
        log.info("User: {}", user);
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(userInfo.getEmail())
                .subject(userInfo.getName())
                .build();

        if (!isRegister) {
            kafkaTemplate.send("notification-delivery", notificationEvent);
        }
        log.info("Send Message Successfully");
        var token = generateToken(user);
        log.info("Token: {}", token);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = userRepostitory
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("nhatanh2709.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        try {
            var signToken = verifyToken(request.getToken(), true);
            String jwtID = signToken.getJWTClaimsSet().getJWTID();
            Date expireTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jwtID).expiryTime(expireTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token already expired");
        }
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken(), true);

        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expireTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expireTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signJWT.getJWTClaimsSet().getSubject();
        var user =
                userRepostitory.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);
        return RefreshTokenResponse.builder().token(token).authenticated(true).build();
    }

    private SignedJWT verifyToken(String token, Boolean isRefresh) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiredTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiredTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
                }
            });
        }
        return stringJoiner.toString();
    }
}

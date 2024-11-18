package com.example.identity_service.service;

import com.example.event.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.List;

import com.example.identity_service.dto.request.ProfileCreationRequest;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.ProfileMapper;
import com.example.identity_service.repository.httpClient.ProfileClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.identity_service.dto.identity.Credential;
import com.example.identity_service.dto.request.ClientExchangeTokenRequest;
import com.example.identity_service.dto.identity.UserCreationParam;
import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.exception.ErrorNormalizer;
import com.example.identity_service.mapper.UserMapper;
import com.example.identity_service.repository.httpClient.IdentityClient;
import com.example.identity_service.repository.UserRepository;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    IdentityClient identityClient;
    ErrorNormalizer errorNormalizer;
    UserRepository userRepository;
    UserMapper userMapper;
    ProfileClient profileClient;
    ProfileMapper profileMapper;
    KafkaTemplate<String , Object> kafkaTemplate;
    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse register(UserCreationRequest request) {
        try {
            if(userRepository.existsByUsername(request.getUsername())) {
                throw new AppException(ErrorCode.USERNAME_EXSITED);
            }
            if(userRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }

            var token = identityClient.exchangeToken(ClientExchangeTokenRequest.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());

            var creationResponse = identityClient.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .firstName(request.getUsername())
                            .lastName(request.getUsername())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value(request.getPassword())
                                    .build()))
                            .build());

            String userId = extractUserId(creationResponse);
            var newUsers = userMapper.toUser(request);
            newUsers.setUserId(userId);
            var user = userRepository.save(newUsers);

            ProfileCreationRequest profileCreationRequest = profileMapper.toProfileCreationRequest(request);
            profileCreationRequest.setUserId(user.getId());
            var result = profileClient.createProfile(profileCreationRequest);

            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject(request.getUsername())
                    .build();

            kafkaTemplate.send("notification-delivery", notificationEvent);

            return userMapper.toUserResponse(user);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    public UserResponse getMyInfo() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        var user = userRepository.findByUserId(userId).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }


    @PostAuthorize("returnObject.userId == authentication.name")
    public UserResponse getUser(String userId) {

        return userMapper.toUserResponse(
                userRepository.findByUserId(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED)
                );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");
        String token = null;

        if(StringUtils.hasText(authHeader)) {
            token = authHeader;
        }
        String userId = user.getUserId();
        identityClient.deleteUser(token, userId);
        userRepository.deleteById(userId);
    }


    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().get("Location").getFirst();
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }



}

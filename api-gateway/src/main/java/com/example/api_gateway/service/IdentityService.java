package com.example.api_gateway.service;


import com.example.api_gateway.dto.ApiResponse;
import com.example.api_gateway.dto.request.IntroSpectRequest;
import com.example.api_gateway.dto.response.IntrospectResponse;
import com.example.api_gateway.repository.httpClient.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return identityClient.introspect(IntroSpectRequest.builder()
                .token(token)
                .build());
    }
}
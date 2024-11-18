package com.example.api_gateway.repository.httpClient;

import com.example.api_gateway.dto.ApiResponse;
import com.example.api_gateway.dto.request.IntroSpectRequest;
import com.example.api_gateway.dto.response.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityClient {
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntroSpectRequest request);
}
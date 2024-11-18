package com.example.identity_service.repository.httpClient;

import com.example.identity_service.dto.request.RefreshTokenExchangeRequest;
import com.example.identity_service.dto.request.UserExchangeTokenRequest;
import com.example.identity_service.dto.response.RefreshTokenExchangeResponse;
import com.example.identity_service.dto.response.UserExchangeTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.identity_service.dto.request.ClientExchangeTokenRequest;
import com.example.identity_service.dto.response.ClientExchangeTokenResponse;
import com.example.identity_service.dto.identity.UserCreationParam;

import feign.QueryMap;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(
            value = "/realms/movie-web/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ClientExchangeTokenResponse exchangeToken(@QueryMap ClientExchangeTokenRequest request);

    @PostMapping(value = "/admin/realms/movie-web/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestHeader("authorization") String token, @RequestBody UserCreationParam param);

    @PostMapping(
            value = "/realms/movie-web/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    UserExchangeTokenResponse exchangeUserToken(@QueryMap UserExchangeTokenRequest request);

    @DeleteMapping(
            value = "/admin/realms/movie-web/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void deleteUser (@RequestHeader("authorization") String token, @PathVariable("userId") String userId);


    @PostMapping(
            value = "/realms/movie-web/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    RefreshTokenExchangeResponse refreshToken(@QueryMap RefreshTokenExchangeRequest request);


}

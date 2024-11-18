package com.example.identity_service.controller;

import com.example.identity_service.dto.ApiResponse;
import com.example.identity_service.dto.request.*;
import com.example.identity_service.dto.response.AuthenticationResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.dto.response.RefreshTokenResponse;
import com.example.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request)
            throws JOSEException, ParseException  {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws JOSEException, ParseException{
        return ApiResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout() throws JOSEException, ParseException {
        authenticationService.logout();
        return ApiResponse.<Void>builder()
                .message("Logout Successfully")
                .build();
    }

    @PostMapping("/refreshToken")
    ApiResponse<RefreshTokenResponse> refreshToken() throws JOSEException, ParseException {
        return ApiResponse.<RefreshTokenResponse>builder()
                .result(authenticationService.refreshToken())
                .build();
    }

    @PostMapping("/forgotPassword")
    ApiResponse<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ApiResponse.<Void>builder()
                .message("Please Check Email To Reset Password")
                .build();
    }

    @PostMapping("/outbound/authentication")
    ApiResponse<AuthenticationResponse> outboundAuthenticate(@RequestParam("code") String code)
            throws JOSEException, ParseException {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.outboundAuthenticate(code))
                .build();
    }
}

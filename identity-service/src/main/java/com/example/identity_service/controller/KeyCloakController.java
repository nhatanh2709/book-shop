package com.example.identity_service.controller;

import com.example.identity_service.dto.ApiResponse;
import com.example.identity_service.service.keycloak.KeyCloakService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keycloak")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KeyCloakController {
    KeyCloakService keyCloakUserService;
    @GetMapping("/{userId}")
    ApiResponse<UserRepresentation> getUserById(@PathVariable String userId) {
        return ApiResponse.<UserRepresentation>builder()
                .result(keyCloakUserService.getUserById(userId))
                .build();
    }

    @PutMapping("/send-verify-email/{userId}")
    ApiResponse<Void> sendVerificationEmail(@PathVariable String userId) {
        keyCloakUserService.emailVerification(userId);
        return ApiResponse.<Void>builder()
                .message("Verify Email Successfully")
                .build();
    }

    @PutMapping("/update-password/{userId}")
    ApiResponse<Void> updatePassword(@PathVariable String userId) {
        keyCloakUserService.updatePassword(userId);
        return ApiResponse.<Void>builder()
                .message("Please Check Email To Update Password")
                .build();
    }

}

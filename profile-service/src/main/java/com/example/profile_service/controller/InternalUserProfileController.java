package com.example.profile_service.controller;

import com.example.profile_service.dto.ApiResponse;
import com.example.profile_service.dto.request.ProfileCreationRequest;
import com.example.profile_service.dto.response.UserProfileResponse;
import com.example.profile_service.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/internal")
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/users")
    ApiResponse<UserProfileResponse> createProfile(@RequestBody ProfileCreationRequest request) {
        log.info("11111");
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.createProfile(request))
                .build();
    }
}

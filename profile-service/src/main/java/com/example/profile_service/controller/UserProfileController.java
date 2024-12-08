package com.example.profile_service.controller;

import com.example.profile_service.dto.ApiResponse;
import com.example.profile_service.dto.request.ProfileCreationRequest;
import com.example.profile_service.dto.request.ProfileUpdateRequest;
import com.example.profile_service.dto.request.UpdateTransactionsRequest;
import com.example.profile_service.dto.response.UserProfileResponse;
import com.example.profile_service.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
@Slf4j

public class UserProfileController {
    UserProfileService userProfileService;


    @GetMapping("/myProfile")
    ApiResponse<UserProfileResponse> getProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getProfile())
                .build();
    }

    @DeleteMapping("/{profileId}")
    ApiResponse<String> deleteProfile(@PathVariable String profileId) {
        userProfileService.deleteProfile(profileId);
        return ApiResponse.<String>builder()
                .result("Delete Profile Successfully")
                .build();
    }

    @GetMapping
    ApiResponse<List<UserProfileResponse>> getAllProfiles() {
        return ApiResponse.<List<UserProfileResponse>>builder()
                .result(userProfileService.getAllProfiles())
                .build();
    }
//
    @PutMapping("/change")
    ApiResponse<UserProfileResponse> updateProfile(@RequestBody ProfileUpdateRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateProfile(request))
                .build();
    }

    @PutMapping("/transactions")
    ApiResponse<UserProfileResponse> updateProfile(@RequestBody UpdateTransactionsRequest request) {
        log.info("Update Transactions");
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateTransactions(request.getEmail()))
                .build();
    }
}

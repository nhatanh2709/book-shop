package com.example.identity_service.controller;

import java.util.List;

import com.example.identity_service.aspect.RequiresCaptcha;
import com.example.identity_service.entity.User;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.identity_service.dto.ApiResponse;
import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @PostMapping("/registration")
    @RequiresCaptcha
    ApiResponse<UserResponse> register(@RequestBody @Valid UserCreationRequest request) {
        log.info("User Creation Request: {}", request);
        return ApiResponse.<UserResponse>builder()
                .result(userService.register(request))
                .build();
    }

    @GetMapping("/getAll")
    ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        var result = userService.getUser(userId);
        return ApiResponse.<UserResponse>builder().result(result).build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> myInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }



}

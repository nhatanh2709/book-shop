package com.example.identity_service.dto.request;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.example.identity_service.validator.DobConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 4, message = "INVALID_USERNAME")
    String username;

    @Nullable
    String userId;

    @Size(min = 4, message = "INVALID_PASSWORD")
    String password;

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    String email;

    @Nullable
    String firstName;

    @Nullable
    String lastName;

    @DobConstraint(min = 2, message = "INVALID_DOB")
    @Nullable
    LocalDate dob;

    @Nullable
    String city;

    @Builder.Default
    String profilePic = null;
}

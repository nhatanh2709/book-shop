package com.example.identity_service.dto.request;

import jakarta.annotation.Nullable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileCreationRequest {
    String userId;

    @Nullable
    String firstName;

    @Nullable
    String email;

    @Nullable
    String lastName;

    @Nullable
    LocalDate dob;

    @Nullable
    String city;

    @Nullable
    String profilePic;

}

package com.example.bill_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    String identityId;
    String firstName;
    String lastName;
    String email;
    LocalDate dob;
    String city;
    String profilePic;
}

package com.example.identity_service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OutboundUserResponse {
    String id;
    String email;
    String verifyEmail;
    String name;
    String givenName;
    String familyName;
    String picture;
    String locale;
}
package com.example.identity_service.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RefreshTokenExchangeResponse {
    String accessToken;
    String expiresIn;
    String refreshExpiresIn;
    String tokenType;
    String idToken;
    String scope;
    String refreshToken;

}

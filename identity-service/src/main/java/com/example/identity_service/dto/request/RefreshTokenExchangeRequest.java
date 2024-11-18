package com.example.identity_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenExchangeRequest {
    String grant_type;
    String client_id;
    String client_secret;
    String refresh_token;
}

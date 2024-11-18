package com.example.identity_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserExchangeTokenRequest {
    String grant_type;
    String client_id;
    String client_secret;
    String scope;
    String username;
    String password;
}

package com.example.profile_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTransactionsRequest {
    String email;
}

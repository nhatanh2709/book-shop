package com.example.identity_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaptchaResponse {
    private Boolean success;
    private Date timestamp;
    private String hostname;
    @JsonProperty("error-codes")
    private List<String> errorCodes;
}

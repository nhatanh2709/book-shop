package com.example.identity_service.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.identity_service.dto.identity.KeyCloakError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ErrorNormalizer {
    private final ObjectMapper objectMapper;
    private final Map<String, ErrorCode> errorCodeMap;

    public ErrorNormalizer() {
        objectMapper = new ObjectMapper();
        errorCodeMap = new HashMap<>();
        errorCodeMap.put("User exists with same email", ErrorCode.EMAIL_EXISTED);
        errorCodeMap.put("User exists with same username", ErrorCode.USERNAME_EXSITED);
        errorCodeMap.put("User name is missing", ErrorCode.USERNAME_IS_MISSING);
    }

    public AppException handleKeyCloakException(FeignException exception) {
        try {
            var response = objectMapper.readValue(exception.contentUTF8(), KeyCloakError.class);
            log.info("Response: {}", response);
            if (Objects.nonNull(response.getErrorMessage())
                    && Objects.nonNull(errorCodeMap.get(response.getErrorMessage()))) {
                return new AppException(errorCodeMap.get(response.getErrorMessage()));
            }
        } catch (JsonProcessingException e) {
            throw  new AppException(ErrorCode.LOGIN_FAILED);
        }
        return new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
}

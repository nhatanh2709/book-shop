package com.example.identity_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    EMAIL_EXISTED(1008, "Email existed , please choose another one", HttpStatus.BAD_REQUEST),
    USERNAME_EXSITED(1009, "Username existed, please choose another one", HttpStatus.BAD_REQUEST),
    USERNAME_IS_MISSING(1010, "Please enter username", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1011, "User not exsited", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1012, "Username must be at least 4 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1013, "Password must be at least 4 characters", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1014, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(1015, "Username or password is wrong", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTED(1016, "This email not existed", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}

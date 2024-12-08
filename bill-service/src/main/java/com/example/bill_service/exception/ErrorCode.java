package com.example.bill_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    RUNTIME_EXCEPTION(9991, "Runtime exception error", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_ARG_NotValid_EXCEPTION(9992, "MethodArgumentNotValidException error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_MESSAGE_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You don't have permissions", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_ROLE(1009, "This role invalid", HttpStatus.FORBIDDEN),
    FIND_MOVIE_BY_SLUG_FAILED(1010, "Find Movie By Slug Failed", HttpStatus.BAD_REQUEST),
    FIND_MOVIE_BY_ID_FAILED(1011, "Find Movie By Id Failed", HttpStatus.BAD_REQUEST),
    FIND_BILL_BY_ID_FAILED(1012, "Find Bill By Id Failed", HttpStatus.BAD_REQUEST),
    CREATE_TRANSACTIONS_FAILED(1013, "Create Transactions Failed", HttpStatus.BAD_REQUEST),
    BILL_NOT_FOUND(1014, "Cannot found Bill Package", HttpStatus.BAD_REQUEST);


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}

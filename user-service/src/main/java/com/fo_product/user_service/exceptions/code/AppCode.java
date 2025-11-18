package com.fo_product.user_service.exceptions.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppCode {
    UNCATEGORIZED_EXCEPTION(1001, "Unknow exception!", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1002, "Unauthenticated", HttpStatus.BAD_REQUEST),
    REQUEST_NULL(1003, "Request is null", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1004, "User is not exist", HttpStatus.BAD_REQUEST),
    USER_EXIST(1005, "User is exist", HttpStatus.BAD_REQUEST),
    CREATE_TOKEN_ERROR(1006, "Cannot create token", HttpStatus.BAD_REQUEST),
    EMAIL_EXIST(1007, "Email is exist", HttpStatus.BAD_REQUEST),

    ;

    private final int code;
    private final String message;
    private final HttpStatus status;

    AppCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}

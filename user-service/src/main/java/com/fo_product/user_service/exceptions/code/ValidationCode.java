package com.fo_product.user_service.exceptions.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ValidationCode {
    UNKNOWN_VALIDATOR(100, "Unknown Validator", HttpStatus.BAD_REQUEST),
    NOT_BLANK(101, "This field must not be blanked", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(102, "Email is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(103, "Please enter the valid password(Must be between 8 to 16 characters and required at least a lowercase letter, a uppercase letter and at least a special character", HttpStatus.BAD_REQUEST),
    NOT_NULL(104, "This field must not null", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus status;

    ValidationCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}

package com.fo_product.common_lib.exceptions.codes;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ValidationCode implements ErrorCode {
    UNKNOWN_VALIDATOR(100, "Unknown Validator", HttpStatus.BAD_REQUEST),
    NOT_BLANK(101, "This field must not be blanked", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(102, "Email is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(103, "Please enter the valid password(Must be between 8 to 16 characters and required at least a lowercase letter, a uppercase letter and at least a special character", HttpStatus.BAD_REQUEST),
    NOT_NULL(104, "This field must not null", HttpStatus.BAD_REQUEST),
    PRICE_ADJUSTMENT_INVALID(105, "PRICE_ADJUSTMENT_INVALID", HttpStatus.BAD_REQUEST),
    NOT_EMPTY(106, "This field must not be empty", HttpStatus.BAD_REQUEST),
    QUANTITY_MIN_1(107, "Quantity atleast 1 item",  HttpStatus.BAD_REQUEST),
    INVALID_STATUS(108, "Invalid status", HttpStatus.BAD_REQUEST),
    STATUS_REQUIRED(109, "Status required", HttpStatus.BAD_REQUEST),
    RATING_MIN_1(110, "Rating at least 1 star",  HttpStatus.BAD_REQUEST),
    RATING_MAX_5(111, "Rating at least 5 stars",  HttpStatus.BAD_REQUEST),

    ;

    int code;
    String message;
    HttpStatus status;
}

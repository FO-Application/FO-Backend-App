package com.fo_product.order_service.exceptions.codes;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public enum ReviewErrorCode implements ErrorCode {
    ORDER_ALREADY_REVIEWED(5001, "Order already reviewed", HttpStatus.CONFLICT),
    REVIEW_NOT_EXIST(5002, "Review not exist", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatus status;
}

package com.fo_product.payment_service.exceptions.codes;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_EXIST(2004, "Order not exist", HttpStatus.NOT_FOUND),
    INVALID_ORDER_STATUS(2007, "Invalid order status", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND_WITH_APP_TRANS_ID(2011, "Order not found with app transId", HttpStatus.NOT_FOUND),

    ;

    int code;
    String message;
    HttpStatus status;
}

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
public enum PaymentErrorCode implements ErrorCode {
    CANT_LINK_UP_ORDER_WITH_PAYMENT_SERVICE(8001, "Cant link up order with payment service", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(8002, "Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE),

    ;

    int code;
    String message;
    HttpStatus status;
}

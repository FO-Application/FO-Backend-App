package com.fo_product.delivery_service.exceptions.code;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public enum DeliveryErrorCode implements ErrorCode {
    ORDER_ALREADY_TAKEN(6001, "Order already taken", HttpStatus.CONFLICT),
    SHIPPER_NOT_FOUND(6002, "Shipper not found", HttpStatus.NOT_FOUND),
    DELIVERY_NOT_FOUND(6003, "Delivery not found", HttpStatus.NOT_FOUND),

    ;

    int code;
    String message;
    HttpStatus status;
}

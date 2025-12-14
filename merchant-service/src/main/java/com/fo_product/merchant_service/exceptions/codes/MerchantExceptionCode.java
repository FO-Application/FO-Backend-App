package com.fo_product.merchant_service.exceptions.codes;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MerchantExceptionCode implements ErrorCode{
    CUISINE_EXIST(3001, "Cuisine exist", HttpStatus.CONFLICT),
    CUISINE_NOT_EXIST(3002, "Cuisine not exist", HttpStatus.NOT_FOUND),
    RESTAURANT_EXIST(3003, "Restaurant exist", HttpStatus.CONFLICT),
    RESTAURANT_NOT_EXIST(3004, "Restaurant not exist", HttpStatus.NOT_FOUND),
    INVALID_MERCHANT_USER_ACCOUNT(3005, "Invalid merchant user account", HttpStatus.BAD_REQUEST),
    RESTAURANT_SCHEDULE_NOT_EXIST(3006, "Restaurant schedule not exist", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXIST(3007, "Category not exist", HttpStatus.NOT_FOUND),

    ;
    int code;
    String message;
    HttpStatus status;
}

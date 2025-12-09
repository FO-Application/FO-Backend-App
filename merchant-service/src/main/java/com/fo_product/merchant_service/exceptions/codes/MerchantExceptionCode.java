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
    SLUG_EXIST(3001, "Slug exist", HttpStatus.CONFLICT),
    CUISINE_NOT_EXIST(3002, "Cuisine not exist", HttpStatus.NOT_FOUND),

    ;
    int code;
    String message;
    HttpStatus status;
}

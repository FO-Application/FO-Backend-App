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
public enum MerchantErrorCode implements ErrorCode{
    CUISINE_EXIST(3001, "Cuisine exist", HttpStatus.CONFLICT),
    CUISINE_NOT_EXIST(3002, "Cuisine not exist", HttpStatus.NOT_FOUND),
    RESTAURANT_EXIST(3003, "Restaurant exist", HttpStatus.CONFLICT),
    RESTAURANT_NOT_EXIST(3004, "Restaurant not exist", HttpStatus.NOT_FOUND),
    INVALID_MERCHANT_USER_ACCOUNT(3005, "Invalid merchant user account", HttpStatus.BAD_REQUEST),
    RESTAURANT_SCHEDULE_NOT_EXIST(3006, "Restaurant schedule not exist", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXIST(3007, "Category not exist", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_EXIST(3008, "Product not exist", HttpStatus.NOT_FOUND),
    OPTION_GROUP_NOT_EXIST(3009, "Option group not exist", HttpStatus.NOT_FOUND),
    SLUG_EXIST(4001, "Slug exist", HttpStatus.CONFLICT),
    SCHEDULE_TIME_NOT_VALID(4002, "Schedule time not valid", HttpStatus.BAD_REQUEST),
    RESTAURANT_SCHEDULE_EXIST(4003, "Restaurant schedule exist", HttpStatus.CONFLICT),
    CATEGORY_EXIST(4004, "Category exist", HttpStatus.CONFLICT),
    PRODUCT_EXIST(4005, "Product exist", HttpStatus.CONFLICT),
    INVALID_PRICE(4006, "Invalid price", HttpStatus.BAD_REQUEST),
    OPTION_GROUP_EXIST(4007, "Option group exist", HttpStatus.BAD_REQUEST),
    INVALID_SELECTION_RANGE(4008, "Invalid selection range", HttpStatus.BAD_REQUEST),
    OPTION_ITEM_NOT_EXIST(4009, "Option item not exist", HttpStatus.NOT_FOUND),
    OPTION_ITEM_EXIST(4010, "Option item exist", HttpStatus.CONFLICT),

    ;
    int code;
    String message;
    HttpStatus status;
}

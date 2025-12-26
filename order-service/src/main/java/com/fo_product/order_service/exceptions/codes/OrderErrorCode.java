package com.fo_product.order_service.exceptions.codes;

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
    PRODUCT_NOT_EXIST(2001, "Product not exist", HttpStatus.UNPROCESSABLE_ENTITY),
    TOPPING_OUT_OF_STOCK(2002, "Topping out of stock", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_OPTION_ID(2003, "Invalid option id", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXIST(2004, "Order not exist", HttpStatus.UNPROCESSABLE_ENTITY),
    ORDER_INVALID(2005, "Order invalid", HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_ORDER(2006, "Cannot cancel order", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(2007, "Invalid order status", HttpStatus.BAD_REQUEST),
    UNFINISHED_ORDER(2008, "Unfinished order", HttpStatus.BAD_REQUEST),
    INVALID_OWNER(2009, "Invalid owner id", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatus status;
}

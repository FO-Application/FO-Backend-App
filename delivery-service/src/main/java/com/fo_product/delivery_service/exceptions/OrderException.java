package com.fo_product.delivery_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.delivery_service.exceptions.code.OrderErrorCode;

public class OrderException extends BaseException {
    public OrderException(OrderErrorCode code) {
        super(code);
    }
}

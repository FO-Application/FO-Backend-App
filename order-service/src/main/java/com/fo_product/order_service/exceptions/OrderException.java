package com.fo_product.order_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;

public class OrderException extends BaseException {
    protected OrderException(OrderErrorCode code) {
        super(code);
    }
}

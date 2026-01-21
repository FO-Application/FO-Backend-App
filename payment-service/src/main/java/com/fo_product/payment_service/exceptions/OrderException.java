package com.fo_product.payment_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.payment_service.exceptions.codes.OrderErrorCode;

public class OrderException extends BaseException {
    public OrderException(OrderErrorCode code) {
        super(code);
    }
}

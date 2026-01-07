package com.fo_product.delivery_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.delivery_service.exceptions.code.DeliveryErrorCode;

public class DeliveryException extends BaseException {
    public DeliveryException(DeliveryErrorCode code) {
        super(code);
    }
}

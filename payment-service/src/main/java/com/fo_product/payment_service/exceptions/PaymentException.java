package com.fo_product.payment_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.payment_service.exceptions.codes.PaymentErrorCode;

public class PaymentException extends BaseException {
    public PaymentException(PaymentErrorCode code) {
        super(code);
    }
}

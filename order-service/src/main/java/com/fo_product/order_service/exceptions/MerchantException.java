package com.fo_product.order_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.order_service.exceptions.codes.MerchantErrorCode;

public class MerchantException extends BaseException {
    public MerchantException(MerchantErrorCode code) {
        super(code);
    }
}

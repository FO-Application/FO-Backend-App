package com.fo_product.merchant_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;

public class MerchantException extends BaseException {
    public MerchantException(MerchantExceptionCode code) {
        super(code);
    }
}

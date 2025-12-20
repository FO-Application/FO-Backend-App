package com.fo_product.merchant_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.merchant_service.exceptions.codes.MinIOErrorCode;

public class MinIOException extends BaseException {
    public MinIOException(MinIOErrorCode code) {
        super(code);
    }
}

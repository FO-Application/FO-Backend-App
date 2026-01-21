package com.fo_product.merchant_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.merchant_service.exceptions.codes.UserErrorCode;

public class UserException extends BaseException {
    public UserException(UserErrorCode code) {
        super(code);
    }
}
package com.fo_product.order_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.order_service.exceptions.codes.UserErrorCode;

public class UserException extends BaseException {
    public UserException(UserErrorCode code) {
        super(code);
    }
}

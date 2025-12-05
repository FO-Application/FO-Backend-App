package com.fo_product.user_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.user_service.exceptions.code.UserExceptionCode;

public class UserException extends BaseException {
    public UserException(UserExceptionCode code) {
        super(code);
    }
}
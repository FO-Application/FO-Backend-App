package com.fo_product.common_lib.exceptions;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseException extends RuntimeException{
    ErrorCode code;

    protected BaseException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}

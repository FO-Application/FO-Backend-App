package com.fo_product.common_lib.exceptions.codes.interfaces;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    int getCode();
    String getMessage();
    HttpStatus getStatus();
}

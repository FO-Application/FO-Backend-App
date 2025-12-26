package com.fo_product.order_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.order_service.exceptions.codes.ReviewErrorCode;

public class ReviewException extends BaseException {
    public ReviewException(ReviewErrorCode code) {
        super(code);
    }
}

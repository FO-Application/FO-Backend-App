package com.fo_product.notification_service.exceptions;

import com.fo_product.common_lib.exceptions.BaseException;
import com.fo_product.notification_service.exceptions.code.NotificationExceptionCode;

public class NotificationException extends BaseException {
    public NotificationException(NotificationExceptionCode code) {
        super(code);
    }
}

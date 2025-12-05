package com.fo_product.notification_service.exceptions.code;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum NotificationExceptionCode implements ErrorCode {
    SEND_MAIL_FAILED(2001, "Send mail failed", HttpStatus.BAD_REQUEST)
    ;

    int code;
    String message;
    HttpStatus status;
}

package com.fo_product.user_service.exceptions.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppCode {
    UNCATEGORIZED_EXCEPTION(1001, "Unknow exception!", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1002, "Unauthenticated", HttpStatus.BAD_REQUEST),
    REQUEST_NULL(1003, "Request is null", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1004, "User is not exist", HttpStatus.BAD_REQUEST),
    USER_EXIST(1005, "User is exist", HttpStatus.BAD_REQUEST),
    CREATE_TOKEN_ERROR(1006, "Cannot create token", HttpStatus.BAD_REQUEST),
    EMAIL_EXIST(1007, "Email is exist", HttpStatus.BAD_REQUEST),
    OTP_NOT_EXIST(1008, "Otp not exist or expired!", HttpStatus.BAD_REQUEST),
    ACCOUNT_EXIST(1009, "This user account is already exist!", HttpStatus.BAD_REQUEST),
    PENDING_USER_NOT_FOUND(1010, "Pending user not found", HttpStatus.BAD_REQUEST),
    SEND_MAIL_FAILED(1011, "Send mail failed!", HttpStatus.BAD_REQUEST),
    VERIFY_OTP_FAILED(1012, "Invalid OTP!", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST(1013, "This role is not exist!", HttpStatus.BAD_REQUEST),

    ;

    private final int code;
    private final String message;
    private final HttpStatus status;

    AppCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}

package com.fo_product.user_service.exceptions.code;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserExceptionCode implements ErrorCode {
    UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.BAD_REQUEST),
    REQUEST_NULL(1002, "Request is null", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1003, "User is not exist", HttpStatus.BAD_REQUEST),
    CREATE_TOKEN_ERROR(1004, "Cannot create token", HttpStatus.BAD_REQUEST),
    EMAIL_EXIST(1005, "Email is exist", HttpStatus.BAD_REQUEST),
    OTP_NOT_EXIST(1006, "Otp not exist or expired!", HttpStatus.BAD_REQUEST),
    ACCOUNT_EXIST(1007, "This user account is already exist!", HttpStatus.BAD_REQUEST),
    PENDING_USER_NOT_FOUND(1008, "Pending user not found", HttpStatus.BAD_REQUEST),
    SEND_MAIL_FAILED(1009, "Send mail failed!", HttpStatus.BAD_REQUEST),
    VERIFY_OTP_FAILED(1010, "Invalid OTP!", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST(1011, "This role is not exist!", HttpStatus.BAD_REQUEST),

    ;

    int code;
    String message;
    HttpStatus status;
}

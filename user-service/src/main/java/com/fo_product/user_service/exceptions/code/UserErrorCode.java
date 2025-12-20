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
public enum UserErrorCode implements ErrorCode {
    UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1002, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),
    REQUEST_NULL(1003, "Request is null", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1004, "User is not exist", HttpStatus.NOT_FOUND),
    CREATE_TOKEN_ERROR(1005, "Cannot create token", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_EXIST(1006, "Email is exist", HttpStatus.CONFLICT),
    OTP_NOT_EXIST(1007, "Otp not exist or expired!", HttpStatus.NOT_FOUND),
    ACCOUNT_EXIST(1008, "This user account is already exist!", HttpStatus.CONFLICT),
    PENDING_USER_NOT_FOUND(1009, "Pending user not found", HttpStatus.NOT_FOUND),
    SEND_MAIL_FAILED(1010, "Send mail failed!", HttpStatus.INTERNAL_SERVER_ERROR),
    VERIFY_OTP_FAILED(1011, "Invalid OTP!", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST(1012, "This role is not exist!", HttpStatus.NOT_FOUND),

    ;

    int code;
    String message;
    HttpStatus status;
}

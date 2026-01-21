package com.fo_product.merchant_service.exceptions.codes;

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
    USER_NOT_EXIST(1004, "User is not exist", HttpStatus.NOT_FOUND),

    ;

    int code;
    String message;
    HttpStatus status;
}

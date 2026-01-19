package com.fo_product.common_lib.exceptions.codes;

import com.fo_product.common_lib.exceptions.codes.interfaces.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum CommonErrorCode implements ErrorCode {
    UNCATEGORIZED_EXCEPTION(9998, "Unknow exception!", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(9999, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    ;

    int code;
    String message;
    HttpStatus status;
}

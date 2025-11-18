package com.fo_product.user_service.exceptions.custom;

import com.fo_product.user_service.exceptions.code.AppCode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppException extends RuntimeException {
    private AppCode appCode;

    public AppException(AppCode appCode) {
        super(appCode.getMessage());
        this.appCode = appCode;
    }

    public AppCode getAppCode() {
        return appCode;
    }

    public void setAppCode(AppCode appCode) {
        this.appCode = appCode;
    }
}

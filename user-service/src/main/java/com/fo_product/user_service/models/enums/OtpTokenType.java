package com.fo_product.user_service.models.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum OtpTokenType {
    REGISTER("Mã OTP xác thực đăng ký tài khoản"),
    FORGOT_PASSWORD("Mã OTP đặt lại mật khẩu")

    ;

    String message;
}

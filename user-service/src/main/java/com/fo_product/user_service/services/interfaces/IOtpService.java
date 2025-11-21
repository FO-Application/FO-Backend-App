package com.fo_product.user_service.services.interfaces;

import com.fo_product.user_service.models.enums.OtpTokenType;

public interface IOtpService {
    void generateAndSendOtp(String email, OtpTokenType type);
    boolean verifyOtp(String email, String otp);
}

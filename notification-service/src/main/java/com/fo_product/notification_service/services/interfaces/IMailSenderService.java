package com.fo_product.notification_service.services.interfaces;

public interface IMailSenderService {
    void sendOtpEmail(String to, String otp, String type);
}

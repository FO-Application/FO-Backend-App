package com.fo_product.notification_service.services.interfaces;

import com.fo_product.notification_service.events.OrderDeliveringEvent;

public interface IMailSenderService {
    void sendOtpEmail(String to, String otp, String type);
    void sendDeliverMail(OrderDeliveringEvent event);
}

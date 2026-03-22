package com.fo_product.merchant_service.kafka.events;

import lombok.Builder;

@Builder
public record MailSenderEvent(
        String recipientEmail,
        String subject,
        String otpCode,
        String eventType
) {
}

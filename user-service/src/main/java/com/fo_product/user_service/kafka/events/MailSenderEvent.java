package com.fo_product.user_service.kafka.events;

import lombok.Builder;

@Builder
public record MailSenderEvent(
        String recipientEmail,
        String subject,
        String otpCode,
        String eventType
) {
}

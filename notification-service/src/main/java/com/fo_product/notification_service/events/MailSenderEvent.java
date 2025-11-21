package com.fo_product.notification_service.events;

import lombok.Builder;

@Builder
public record MailSenderEvent(
        String recipientEmail,
        String subject,
        String otpCode,
        String eventType
) {
}

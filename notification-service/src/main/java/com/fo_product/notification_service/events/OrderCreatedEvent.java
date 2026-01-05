package com.fo_product.notification_service.events;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderCreatedEvent(
        Long orderId,
        Long merchantId,
        String merchantName,
        String customerName,
        BigDecimal grandTotal,
        LocalDateTime createdAt,
        List<String> itemNames
) {
}

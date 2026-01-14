package com.fo_product.notification_service.events;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCancelledEvent {
    Long orderId;
    Long merchantId;
    Long userId;
    String cancelledBy; // "CUSTOMER" hoặc "MERCHANT"
    String reason;
    BigDecimal amount;
    String paymentMethod;
    LocalDateTime cancelledAt;
}

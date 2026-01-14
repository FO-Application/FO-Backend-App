package com.fo_product.order_service.kafka.events;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderPaidEvent {
    Long orderId;
    Long merchantId;
    Long ownerId;
    BigDecimal amount;
    String paymentMethod;
    LocalDateTime paidAt;
}
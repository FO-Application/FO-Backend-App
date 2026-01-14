package com.fo_product.notification_service.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderPaidEvent {
    Long orderId;
    Long merchantId;
    Long ownerId;
    BigDecimal amount;
    String paymentMethod;
    LocalDateTime paidAt;
}

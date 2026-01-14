package com.fo_product.order_service.kafka.events;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderReadyEvent {
    Long orderId;
    Long merchantId;
    Long shipperId; // Nếu đã có shipper thì gửi kèm để noti
}

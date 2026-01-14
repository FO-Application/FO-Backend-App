package com.fo_product.notification_service.events;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipperAssignedEvent {
    Long orderId;
    Long shipperId;
    String shipperName;
    String shipperPhone;
    String licensePlate;
}
package com.fo_product.order_service.kafka.events;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewCreatedEvent {
    Long merchantId;
    Double rating;
}

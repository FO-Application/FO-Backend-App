package com.fo_product.delivery_service.services.interfaces;

import com.fo_product.delivery_service.kafka.events.OrderConfirmedEvent;

public interface IOrderMatchingService {
    void processMatching(OrderConfirmedEvent orderConfirmedEvent);
    void retryFindingShippers();
}

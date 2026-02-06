package com.fo_product.delivery_service.services.interfaces;

import com.fo_product.delivery_service.kafka.events.OrderConfirmedEvent;
import com.fo_product.delivery_service.kafka.events.ShipperFoundEvent;

import java.util.List;

public interface IOrderMatchingService {
    void processMatching(OrderConfirmedEvent orderConfirmedEvent);
    void addToPendingQueue(OrderConfirmedEvent event);
    void removeFromPendingQueue(Long orderId);
    List<ShipperFoundEvent> getPendingOffers(Long shipperId);
}

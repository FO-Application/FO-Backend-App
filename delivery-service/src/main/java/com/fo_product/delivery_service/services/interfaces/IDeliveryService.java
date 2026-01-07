package com.fo_product.delivery_service.services.interfaces;

public interface IDeliveryService {
    void acceptOrder(Long userId, Long orderId);
    void updatePickedUp(Long userId, Long orderId);
    void completeOrder(Long userId, Long orderId);
}

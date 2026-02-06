package com.fo_product.delivery_service.services.interfaces;

public interface IDeliveryService {
    void acceptOrder(Long userId, Long orderId);
    void updatePickedUp(Long userId, Long orderId);
    void completeOrder(Long userId, Long orderId);
    void deposit(Long userId, java.math.BigDecimal amount);
    com.fo_product.delivery_service.dtos.responses.ShipperWalletResponse getWalletStats(Long userId);
}

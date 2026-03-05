package com.fo_product.delivery_service.services.interfaces;

import com.fo_product.delivery_service.dtos.responses.ShipperWalletResponse;

public interface IDeliveryService {
    void acceptOrder(Long userId, Long orderId);
    void updatePickedUp(Long userId, Long orderId);
    void completeOrder(Long userId, Long orderId);
    void deposit(Long userId, java.math.BigDecimal amount);
    ShipperWalletResponse getWalletStats(Long userId);
}

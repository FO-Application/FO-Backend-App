package com.fo_product.order_service.services.interfaces;

public interface IInternalOrderService {
    void updateAppTransId(Long orderId, String appTransId);
    void updateOrderStatusByAppTransId(String appTransId, String status);
}

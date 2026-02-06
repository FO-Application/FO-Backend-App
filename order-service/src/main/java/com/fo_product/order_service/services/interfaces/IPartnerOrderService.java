package com.fo_product.order_service.services.interfaces;

import com.fo_product.order_service.dtos.responses.MerchantStatsResponse;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import org.springframework.data.domain.Page;

public interface IPartnerOrderService {
    Page<OrderResponse> getOrdersByMerchant(Long userId, Long merchantId, String status, int page, int size);
    OrderResponse confirmAndPrepareOrder(Long userId, Long orderId);
    OrderResponse cancelOrder(Long userId, Long orderId);
    void markOrderAsDelivering(Long orderId);
    void markOrderAsCompleted(Long orderId);
    OrderResponse markOrderAsReady(Long userId, Long orderId);
    OrderResponse getOrderById(Long orderId);
    MerchantStatsResponse getMerchantStats(Long userId, Long merchantId);
}

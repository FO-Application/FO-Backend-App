package com.fo_product.order_service.services.interfaces;

import com.fo_product.order_service.dtos.requests.OrderRequest;
import com.fo_product.order_service.dtos.requests.UpdateOrderStatusRequest;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import org.springframework.data.domain.Page;

public interface IOrderService {
    OrderResponse createOrder(Long userId, OrderRequest request);
    Page<OrderResponse> getMyOrders(Long userId, int page, int size);
    OrderResponse getOrderById(Long userId, Long orderId);
    OrderResponse cancelOrder(Long userId, Long orderId);
    Page<OrderResponse> getOrdersByMerchant(Long userId, Long merchantId, String status, int page, int size);
    OrderResponse updateOrderStatus(Long userId, Long orderId, UpdateOrderStatusRequest request);
}

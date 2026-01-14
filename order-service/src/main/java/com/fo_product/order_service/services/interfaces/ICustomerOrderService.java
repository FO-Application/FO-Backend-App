package com.fo_product.order_service.services.interfaces;

import com.fo_product.order_service.dtos.requests.OrderRequest;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.dtos.responses.OrderStatusResponse;
import org.springframework.data.domain.Page;

public interface ICustomerOrderService {
    OrderResponse createOrder(Long userId, OrderRequest request);
    OrderStatusResponse checkOrderStatus(Long orderId);
    Page<OrderResponse> getMyOrders(Long userId, int page, int size);
    OrderResponse getOrderById(Long userId, Long orderId);
    OrderResponse cancelOrder(Long userId, Long orderId);
}

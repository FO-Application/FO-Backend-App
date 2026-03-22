package com.fo_product.order_service.services.interfaces;

import com.fo_product.order_service.dtos.responses.OrderResponse;
import org.springframework.data.domain.Page;

public interface IAdminOrderService {
    Page<OrderResponse> getAllOrders(int page, int size, String status);
    com.fo_product.order_service.dtos.responses.DashboardStatsResponse getDashboardStats(int days);
}

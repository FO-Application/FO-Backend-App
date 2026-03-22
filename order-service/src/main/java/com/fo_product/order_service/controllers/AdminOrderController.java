package com.fo_product.order_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.services.interfaces.IAdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/management/order/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Admin Order Controller", description = "Quản lý toàn bộ đơn hàng thống kê hệ thống dành cho Super Admin")
public class AdminOrderController {
    IAdminOrderService adminOrderService;

    @Operation(summary = "Lấy danh sách tất cả các đơn hàng hệ thống", description = "Dành cho Dashboard Super Admin")
    @GetMapping
    public APIResponse<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderResponse> result = adminOrderService.getAllOrders(page, size, status);
        return APIResponse.<Page<OrderResponse>>builder()
                .result(result)
                .message("Get all orders successfully")
                .build();
    }
    @Operation(summary = "Lấy thống kê biểu đồ hệ thống", description = "Dành cho Dashboard Super Admin")
    @GetMapping("/statistics")
    public APIResponse<com.fo_product.order_service.dtos.responses.DashboardStatsResponse> getDashboardStats(
            @RequestParam(defaultValue = "7") int days
    ) {
        return APIResponse.<com.fo_product.order_service.dtos.responses.DashboardStatsResponse>builder()
                .result(adminOrderService.getDashboardStats(days))
                .message("Get dashboard stats successfully")
                .build();
    }
}

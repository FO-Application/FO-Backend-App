package com.fo_product.order_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.services.interfaces.IPartnerOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipping/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Delivery Order Controller", description = "API Quản lý đơn hàng dành cho Shipper")
public class DeliveryOrderController {
    IPartnerOrderService orderService;

    @Operation(summary = "Lấy chi tiết đơn hàng (Dành cho Delivery Service/Feign)")
    @GetMapping("/{id}")
    public APIResponse<OrderResponse> getOrderInternal(@PathVariable("id") Long orderId) {
        return APIResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(orderId))
                .build();
    }

    @Operation(summary = "Cập nhật trạng thái: Đang giao hàng (Shipper đã lấy món)")
    @PutMapping("/{id}/delivering")
    public APIResponse<Void> markAsDelivering(@PathVariable("id") Long orderId) {
        orderService.markOrderAsDelivering(orderId);
        return APIResponse.<Void>builder()
                .message("Status updated to DELIVERING")
                .build();
    }

    @Operation(summary = "Cập nhật trạng thái: Giao thành công (Shipper hoàn tất)")
    @PutMapping("/{id}/completed")
    public APIResponse<Void> markAsCompleted(@PathVariable("id") Long orderId) {
        orderService.markOrderAsCompleted(orderId);
        return APIResponse.<Void>builder()
                .message("Status updated to COMPLETED")
                .build();
    }
}

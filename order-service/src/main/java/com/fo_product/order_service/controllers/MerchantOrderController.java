package com.fo_product.order_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.dtos.requests.UpdateOrderStatusRequest;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.services.interfaces.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order/merchant")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Merchant Order Controller", description = "API Quản lý đơn hàng dành cho Chủ Quán")
public class MerchantOrderController {
    IOrderService orderService;

    @Operation(summary = "Lấy danh sách đơn hàng của Quán", description = "Có thể lọc theo trạng thái (vd: CREATED để xem đơn mới cần duyệt)")
    @GetMapping("/{id}")
    public APIResponse<Page<OrderResponse>> getMerchantOrders(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());

        Page<OrderResponse> result = orderService.getOrdersByMerchant(userId, merchantId,status, page, size);
        return APIResponse.<Page<OrderResponse>>builder()
                .result(result)
                .message("Get merchant orders success")
                .build();
    }

    @Operation(summary = "Cập nhật trạng thái đơn hàng", description = "Chuyển trạng thái: Nhận đơn -> Nấu -> Giao -> Hoàn thành")
    @PatchMapping("/orders/{orderId}/status")
    public APIResponse<OrderResponse> updateStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());

        OrderResponse result = orderService.updateOrderStatus(userId, orderId, request);
        return APIResponse.<OrderResponse>builder()
                .result(result)
                .message("Update status success")
                .build();
    }
}
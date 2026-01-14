package com.fo_product.order_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.services.interfaces.IPartnerOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/management/order/merchant")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Partner Order Controller", description = "API Quản lý đơn hàng dành cho Chủ Quán")
public class MerchantOrderController {
    IPartnerOrderService orderService;

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

    @Operation(
            summary = "Chủ quán xác nhận đơn hàng (Bắt đầu nấu)",
            description = "Chuyển trạng thái từ CREATED/PAID -> PREPARING. Hệ thống sẽ tự động bắn event tìm Tài xế."
    )
    @PutMapping("/{id}/confirm")
    public APIResponse<OrderResponse> confirmOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long orderId
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());

        OrderResponse result = orderService.confirmAndPrepareOrder(userId, orderId);

        return APIResponse.<OrderResponse>builder()
                .result(result)
                .message("Đã xác nhận đơn hàng và bắt đầu tìm tài xế")
                .build();
    }

    @Operation(
            summary = "Chủ quán báo món đã làm xong (Sẵn sàng giao)",
            description = "Chuyển trạng thái từ PREPARING -> READY. Báo hiệu cho Shipper có thể vào lấy món."
    )
    @PutMapping("/{id}/ready")
    public APIResponse<OrderResponse> markAsReady(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long orderId
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());

        OrderResponse result = orderService.markOrderAsReady(userId, orderId);

        return APIResponse.<OrderResponse>builder()
                .result(result)
                .message("Món ăn đã sẵn sàng (READY). Chờ shipper lấy hàng.")
                .build();
    }

    @Operation(
            summary = "Chủ quán hủy đơn hàng",
            description = "Chỉ hủy được khi đơn ở trạng thái CREATED, PAID hoặc PREPARING."
    )
    @PutMapping("/{id}/cancel")
    public APIResponse<OrderResponse> cancelOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long orderId
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());

        OrderResponse result = orderService.cancelOrder(userId, orderId);

        return APIResponse.<OrderResponse>builder()
                .result(result)
                .message("Đã hủy đơn hàng thành công")
                .build();
    }
}
package com.fo_product.order_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.services.interfaces.IInternalOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Partner Order Controller", description = "API Quản lý đơn hàng dành cho Chủ Quán")
public class InternalOrderController {
    IInternalOrderService internalOrderService;

    @PostMapping("/{orderId}/update-trans-id")
    public APIResponse<Void> updateAppTransId(@PathVariable Long orderId, @RequestParam String appTransId) {
        internalOrderService.updateAppTransId(orderId, appTransId);
        return APIResponse.<Void>builder()
                .message("Successfully updated App Trans Id")
                .build();
    }

    @PostMapping("/update-status-by-trans-id")
    public APIResponse<Void> updateStatusByTransId(@RequestParam String appTransId, @RequestParam String status) {
        internalOrderService.updateOrderStatusByAppTransId(appTransId, status);
        return APIResponse.<Void>builder()
                .message("Successfully updated status by App Trans Id")
                .build();
    }
}

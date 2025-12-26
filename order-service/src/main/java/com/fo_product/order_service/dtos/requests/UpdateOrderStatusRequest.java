package com.fo_product.order_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Yêu cầu cập nhật trạng thái đơn hàng (Dành cho Merchant)")
public record UpdateOrderStatusRequest(
        @Schema(
                description = "Trạng thái mới muốn cập nhật. Quy tắc: CREATED -> CONFIRMED -> PREPARING -> DELIVERING -> COMPLETED (Hoặc CANCELLED)",
                example = "CONFIRMED",
                allowableValues = {"CONFIRMED", "PREPARING", "DELIVERING", "COMPLETED", "CANCELLED"}
        )
        @NotNull(message = "STATUS_REQUIRED")
        @Pattern(regexp = "^(CONFIRMED|PREPARING|DELIVERING|COMPLETED|CANCELLED)$", message = "INVALID_STATUS")
        String status
) {
}
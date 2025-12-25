package com.fo_product.order_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemOptionResponse(
        @Schema(description = "Tên nhóm tùy chọn", example = "Mức đường")
        String optionGroupName,

        @Schema(description = "Tên tùy chọn", example = "50% Đường")
        String optionName,

        @Schema(description = "Giá tiền cộng thêm cho tùy chọn này", example = "0")
        BigDecimal priceAdjustment
) {
}

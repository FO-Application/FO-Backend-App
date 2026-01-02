package com.fo_product.delivery_service.dtos.feigns;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record OrderItemOptionDTO (
        @Schema(description = "Tên nhóm tùy chọn", example = "Mức đường")
        String optionGroupName,

        @Schema(description = "Tên tùy chọn", example = "50% Đường")
        String optionName,

        @Schema(description = "Giá tiền cộng thêm cho tùy chọn này", example = "0")
        BigDecimal priceAdjustment
){
}

package com.fo_product.order_service.dtos.feigns;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record OptionItemDTO(
        @Schema(description = "ID định danh của tùy chọn", example = "105")
        Long id,

        @Schema(description = "Tên tùy chọn", example = "Trân châu trắng")
        String name,

        @Schema(description = "Giá tiền cộng thêm (VND)", example = "5000")
        BigDecimal priceAdjustment,

        @Schema(description = "Trạng thái hiển thị (Còn hàng/Hết hàng)", example = "true")
        boolean isAvailable,

        @Schema(description = "Tên của nhóm chứa tùy chọn này (để tiện hiển thị)", example = "Topping")
        String optionGroupName
) {
}

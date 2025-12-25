package com.fo_product.merchant_service.dtos.responses.addon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record OptionItemResponse(
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
) implements Serializable {
}
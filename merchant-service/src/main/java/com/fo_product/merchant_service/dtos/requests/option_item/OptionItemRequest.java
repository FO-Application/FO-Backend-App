package com.fo_product.merchant_service.dtos.requests.option_item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OptionItemRequest(
        @Schema(description = "Tên của tùy chọn chi tiết", example = "Trân châu trắng")
        @NotBlank(message = "NOT_BLANK")
        String name,

        @Schema(description = "Giá tiền cộng thêm khi chọn option này (VND)", example = "5000")
        @NotNull(message = "NOT_NULL")
        @Min(value = 0, message = "PRICE_MUST_BE_POSITIVE")
        BigDecimal priceAdjustment,

        @Schema(description = "ID của nhóm tùy chọn (Option Group) chứa item này", example = "10")
        @NotNull(message = "NOT_NULL")
        long optionGroupId
) {
}
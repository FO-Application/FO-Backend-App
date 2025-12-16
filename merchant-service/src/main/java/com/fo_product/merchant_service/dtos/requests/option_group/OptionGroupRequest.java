package com.fo_product.merchant_service.dtos.requests.option_group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OptionGroupRequest(
        @Schema(description = "Tên nhóm tùy chọn", example = "Chọn mức đường")
        @NotBlank(message = "NOT_BLANK")
        String name,

        @Schema(description = "Số lượng chọn tối thiểu (0 nếu không bắt buộc)", example = "1")
        @NotNull(message = "NOT_NULL")
        int minSelection,

        @Schema(description = "Có bắt buộc phải chọn hay không?", example = "true")
        @NotNull(message = "NOT_NULL")
        boolean isMandatory,

        @Schema(description = "Số lượng chọn tối đa", example = "1")
        @NotNull(message = "NOT_NULL")
        int maxSelection,

        @Schema(description = "ID sản phẩm liên kết", example = "100")
        @NotNull(message = "NOT_NULL")
        Long productId
) {
}
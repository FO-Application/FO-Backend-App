package com.fo_product.merchant_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record OptionGroupResponse(
        @Schema(description = "ID nhóm tùy chọn", example = "50")
        Long id,

        @Schema(description = "Tên nhóm", example = "Chọn mức đường")
        String name,

        @Schema(description = "Bắt buộc chọn?", example = "true")
        boolean isMandatory,

        @Schema(description = "Chọn tối thiểu", example = "1")
        int minSelection,

        @Schema(description = "Chọn tối đa", example = "1")
        int maxSelection,

        @Schema(description = "Tên sản phẩm liên kết", example = "Trà sữa chân trâu")
        String productName
) {
}
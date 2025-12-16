package com.fo_product.merchant_service.dtos.requests.option_group;

import io.swagger.v3.oas.annotations.media.Schema;

public record OptionGroupPatchRequest(
        @Schema(description = "Tên nhóm mới", example = "Chọn Size (S/M/L)")
        String name,

        @Schema(description = "Min selection mới", example = "1")
        Integer minSelection,

        @Schema(description = "Max selection mới", example = "1")
        Integer maxSelection,

        @Schema(description = "Trạng thái bắt buộc mới", example = "true")
        Boolean isMandatory
) {
}
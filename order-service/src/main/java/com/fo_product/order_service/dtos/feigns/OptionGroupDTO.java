package com.fo_product.order_service.dtos.feigns;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OptionGroupDTO (
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
        String productName,

        @Schema(description = "Danh sách các tùy chọn con bên trong nhóm này")
        List<OptionItemDTO> options
){
}

package com.fo_product.merchant_service.dtos.requests.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @Schema(description = "Tên loại thực đơn trong nhà hàng", example = "Món khai vị")
        @NotBlank(message = "NOT_BLANK")
        String name,

        @Schema(description = "Thứ tự hiển thị trên menu, số nhỏ hiện trước", example = "1")
        @NotBlank(message = "NOT_BLANK")
        int displayOrder,

        @Schema(description = "Nhà hàng sở hữu danh mục này", example = "1")
        @NotBlank(message = "NOT_BLANK")
        long idRestaurant
) {
}

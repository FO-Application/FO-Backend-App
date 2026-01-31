package com.fo_product.merchant_service.dtos.requests.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @Schema(description = "Tên loại thực đơn trong nhà hàng", example = "Món khai vị")
        @NotBlank(message = "NOT_BLANK")
        String name,

        @Schema(description = "Thứ tự hiển thị trên menu, số nhỏ hiện trước", example = "1")
        @NotNull(message = "NOT_NULL")
        int displayOrder,

        @Schema(description = "Nhà hàng sở hữu danh mục này", example = "1")
        @NotNull(message = "NOT_NULL")
        long idRestaurant
) {
}

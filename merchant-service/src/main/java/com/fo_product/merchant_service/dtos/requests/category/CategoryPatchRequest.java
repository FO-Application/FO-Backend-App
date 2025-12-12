package com.fo_product.merchant_service.dtos.requests.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoryPatchRequest(
        @Schema(description = "Tên mới (nếu muốn đổi)", example = "Món chính")
        String name,

        @Schema(description = "Thứ tự hiển thị trên menu mới (nếu muốn đổi)", example = "2")
        int displayOrder,

        @Schema(description = "Nhà hàng mới (nếu muốn đổi)", example = "2")
        long idRestaurant,

        @Schema(description = "Ẩn/ hiện danh mục này trên client (Nếu muốn đổi)", example = "false")
        boolean isActive
) {
}

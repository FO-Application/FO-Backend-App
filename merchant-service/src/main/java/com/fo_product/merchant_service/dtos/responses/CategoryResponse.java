package com.fo_product.merchant_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record CategoryResponse(
        @Schema(description = "ID của phân loại thực đơn trong nhà hàng", example = "1")
        Long id,

        @Schema(description = "Tên của phân loại thực đơn trong nhà hàng", example = "Đồ uống")
        String name,

        @Schema(description = "Thứ tự hiển thị trên menu (Ưu tiên số nhỏ hiện trước)", example = "1")
        int displayOrder,

        @Schema(description = "Ẩn/ hiện danh mục này trên client", example = "true")
        boolean isActive,

        @Schema(description = "Response trả về tên nhà hàng", example = "Lẩu Halidao")
        String restaurantName
) implements Serializable {
}

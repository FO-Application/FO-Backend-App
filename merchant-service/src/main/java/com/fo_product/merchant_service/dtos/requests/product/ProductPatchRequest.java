package com.fo_product.merchant_service.dtos.requests.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ProductPatchRequest(
        @Schema(description = "Tên món ăn được bán ra", example = "Phở bò tái lăn")
        String name,

        @Schema(description = "Thông tin về món ăn")
        String description,

        @Schema(description = "Thông tin giá bán(Đã tính cả giá sale)", example = "40000.00")
        BigDecimal price,

        @Schema(description = "Thông tin giá bán gốc của sản phẩm", example = "40000.00")
        BigDecimal originalPrice,

        @Schema(description = "Trạng thái của đồ ăn ẩn hoặc hiển thị trên giao diện", example = "true")
        Boolean status,

        @Schema(description = "ID của nhà hàng sở hữu món ăn", example = "1")
        Long categoryId
) {
}

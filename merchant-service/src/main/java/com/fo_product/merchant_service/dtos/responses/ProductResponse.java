package com.fo_product.merchant_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record ProductResponse(
        @Schema(description = "ID của món ăn")
        Long id,

        @Schema(description = "Tên món ăn được bán ra", example = "Phở bò tái lăn")
        String name,

        @Schema(description = "Thông tin về món ăn")
        String description,

        @Schema(description = "Thông tin giá bán(Đã tính cả giá sale)", example = "40000.00")
        BigDecimal price,

        @Schema(description = "Thông tin giá bán gốc của sản phẩm", example = "40000.00")
        BigDecimal originalPrice,

        @Schema(description = "Link ảnh (Cloudinary/S3...)", example = "https://res.cloudinary.com/.../kimbap.jpg")
        String imageUrl,

        @Schema(description = "Trạng thái của đồ ăn ẩn hoặc hiển thị trên giao diện", example = "true")
        boolean status,

        @Schema(description = "Tên của phân loại món trong nhà hàng", example = "Món chính")
        String categoryName
) implements Serializable {
}

package com.fo_product.merchant_service.dtos.requests.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(

        @Schema(description = "Tên món ăn được bán ra", example = "Phở bò tái lăn")
        @NotBlank(message = "NOT_BLANK")
        String name,

        @Schema(description = "Thông tin về món ăn")
        @NotBlank(message = "NOT_BLANK")
        String description,

        @Schema(description = "Thông tin giá bán(Đã tính cả giá sale)", example = "40000.00")
        @NotNull(message = "NOT_NULL")
        BigDecimal price,

        @Schema(description = "Thông tin giá bán gốc của sản phẩm", example = "40000.00")
        @NotNull(message = "NOT_NULL")
        BigDecimal originalPrice,

        @Schema(description = "ID của nhà hàng sở hữu món ăn", example = "1")
        @NotNull(message = "NOT_NULL")
        Long categoryId
) {
}

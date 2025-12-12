package com.fo_product.merchant_service.dtos.requests.cuisine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CuisineRequest(
        @Schema(description = "Tên loại ẩm thực", example = "Món Việt")
        @NotBlank(message = "NOT_BLANK")
        String name,

        @Schema(description = "Slug URL (viết thường, không dấu, nối gạch ngang)", example = "mon-viet")
        @NotBlank(message = "NOT_BLANK")
        String slug
) {
}
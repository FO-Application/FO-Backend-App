package com.fo_product.merchant_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record CuisineResponse(
        @Schema(description = "ID Cuisine", example = "10")
        Long id,

        @Schema(description = "Tên Cuisine", example = "Món Hàn")
        String name,

        @Schema(description = "Slug URL", example = "mon-han")
        String slug,

        @Schema(description = "Link ảnh (Cloudinary/S3...)", example = "https://res.cloudinary.com/.../kimbap.jpg")
        String imageFileUrl
) implements Serializable {
}
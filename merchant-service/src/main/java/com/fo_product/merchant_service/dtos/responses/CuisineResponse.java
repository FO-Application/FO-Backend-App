package com.fo_product.merchant_service.dtos.responses;

import lombok.Builder;

@Builder
public record CuisineResponse(
    Long id,
    String name,
    String slug,
    String imageFileName
) {
}

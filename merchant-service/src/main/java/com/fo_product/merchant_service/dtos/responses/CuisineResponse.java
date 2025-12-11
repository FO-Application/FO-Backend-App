package com.fo_product.merchant_service.dtos.responses;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record CuisineResponse(
    Long id,
    String name,
    String slug,
    String imageFileUrl
) implements Serializable {
}

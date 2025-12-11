package com.fo_product.merchant_service.dtos.requests.cuisine;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record CuisineRequest(
        @NotBlank(message = "NOT_BLANK")
        String name,
        @NotBlank(message = "NOT_BLANK")
        String slug
) {
}

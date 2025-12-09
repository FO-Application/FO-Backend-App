package com.fo_product.merchant_service.dtos.requests.cuisine;

import org.springframework.web.multipart.MultipartFile;

public record CuisinePatchRequest(
        String name,
        String slug,
        MultipartFile image
) {
}

package com.fo_product.merchant_service.dtos.requests.cuisine;

import io.swagger.v3.oas.annotations.media.Schema;

public record CuisinePatchRequest(
        @Schema(description = "Tên mới (nếu muốn đổi)", example = "Ẩm thực Việt Nam")
        String name,

        @Schema(description = "Slug mới (nếu muốn đổi)", example = "am-thuc-viet-nam")
        String slug
) {
}
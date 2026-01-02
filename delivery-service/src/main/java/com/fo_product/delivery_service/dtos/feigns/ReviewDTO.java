package com.fo_product.delivery_service.dtos.feigns;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReviewDTO(
        @Schema(description = "ID của đánh giá", example = "50")
        Long id,

        @Schema(description = "ID người viết đánh giá", example = "10")
        Long userId,

        @Schema(description = "Số sao đã chấm", example = "5.0")
        Double rating,

        @Schema(description = "Nội dung bình luận", example = "Rất ngon, sẽ ủng hộ tiếp")
        String comment,

        @Schema(description = "Thời gian đánh giá", example = "2024-01-01T12:00:00")
        LocalDateTime createdAt
) {
}

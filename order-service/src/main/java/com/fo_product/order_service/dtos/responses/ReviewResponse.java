package com.fo_product.order_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Dữ liệu phản hồi chi tiết đánh giá")
public record ReviewResponse(
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
package com.fo_product.order_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yêu cầu tạo đánh giá mới")
public record ReviewRequest(
        @Schema(description = "ID của đơn hàng muốn đánh giá", example = "1001")
        @NotNull(message = "NOT_NULL")
        Long orderId,

        @Schema(description = "Số sao đánh giá (Từ 1.0 đến 5.0)", example = "4.5")
        @NotNull(message = "NOT_NULL")
        @Min(value = 1, message = "RATING_MIN_1")
        @Max(value = 5, message = "RATING_MAX_5")
        Double rating,

        @Schema(description = "Nội dung bình luận (Không bắt buộc)", example = "Đồ ăn nóng hổi, giao hàng nhanh!")
        String comment
) {
}
package com.fo_product.merchant_service.dtos.requests.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record RestaurantSchedulePatchRequest(
        @Schema(description = "Thứ trong tuần (Optional)", example = "3")
        Integer dayOfWeek,

        @Schema(description = "Giờ mở cửa mới", example = "09:00:00", type = "string", format = "time")
        LocalTime openTime,

        @Schema(description = "Giờ đóng cửa mới", example = "23:00:00", type = "string", format = "time")
        LocalTime closeTime,

        @Schema(description = "ID nhà hàng (Thường không cho sửa trường này, nhưng giữ nếu cần)", example = "10")
        Long restaurantId
) {
}
package com.fo_product.merchant_service.dtos.requests.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record RestaurantScheduleRequest(
        @Schema(description = "Thứ trong tuần (2=Thứ Hai ... 8=Chủ Nhật)", example = "2")
        @NotNull(message = "NOT_NULL")
        int dayOfWeek,

        @Schema(description = "Giờ mở cửa (HH:mm:ss)", example = "08:00:00", type = "string", format = "time")
        @NotNull(message = "NOT_NULL")
        LocalTime openTime,

        @Schema(description = "Giờ đóng cửa (HH:mm:ss)", example = "22:00:00", type = "string", format = "time")
        @NotNull(message = "NOT_NULL")
        LocalTime closeTime,

        @Schema(description = "ID nhà hàng", example = "10")
        @NotNull(message = "NOT_NULL")
        Long restaurantId
) {
}
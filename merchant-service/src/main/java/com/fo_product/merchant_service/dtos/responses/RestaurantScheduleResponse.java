package com.fo_product.merchant_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalTime;

@Builder
public record RestaurantScheduleResponse(
        @Schema(description = "ID lịch trình", example = "100")
        Long id,

        @Schema(description = "Thứ", example = "2")
        int dayOfWeek,

        @Schema(description = "Giờ mở cửa", example = "08:00:00")
        LocalTime openTime,

        @Schema(description = "Giờ đóng cửa", example = "22:00:00")
        LocalTime closeTime,

        @Schema(description = "Thông tin nhà hàng")
        RestaurantResponse restaurantResponse
) implements Serializable {
}
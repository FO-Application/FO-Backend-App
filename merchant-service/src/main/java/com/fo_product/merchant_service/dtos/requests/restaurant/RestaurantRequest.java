package com.fo_product.merchant_service.dtos.requests.restaurant;

import com.fo_product.common_lib.exceptions.validation.constraint.PhoneConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RestaurantRequest(
        @Schema(description = "Tên nhà hàng", example = "Phở Cồ Truyền Thống")
        @NotBlank(message = "NOT_BLANK")
        String name,

        @Schema(description = "ID của chủ nhà hàng (User ID)", example = "101")
        @NotNull(message = "NOT_NULL")
        Long ownerId,

        @Schema(description = "Mô tả nhà hàng", example = "Chuyên phục vụ các món phở bò, gà...")
        String description,

        @Schema(description = "Slug URL", example = "pho-co-truyen-thong")
        String slug,

        @Schema(description = "Địa chỉ chi tiết", example = "123 Đường Láng, Hà Nội")
        @NotBlank(message = "NOT_BLANK")
        String address,

        @Schema(description = "Vĩ độ (Latitude)", example = "21.028511")
        @NotNull(message = "NOT_NULL")
        Double latitude,

        @Schema(description = "Kinh độ (Longitude)", example = "105.854444")
        @NotNull(message = "NOT_NULL")
        Double longitude,

        @Schema(description = "Số điện thoại hotline", example = "0987654321")
        @NotBlank(message = "NOT_BLANK")
        @PhoneConstraint(length = 10, message = "INVALID_PHONE")
        String phone,

        @Schema(description = "Danh sách các thẻ tag mà nhà hàng đăng ký", example = "[1,2]")
        @NotNull(message = "NOT_NULL")
        Set<Long> cuisinesId
) {
}
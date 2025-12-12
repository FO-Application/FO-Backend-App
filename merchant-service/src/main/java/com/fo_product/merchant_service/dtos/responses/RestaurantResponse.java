package com.fo_product.merchant_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record RestaurantResponse(
        @Schema(description = "ID nhà hàng", example = "1")
        Long id,

        @Schema(description = "Tên nhà hàng", example = "Phở Cồ")
        String name,

        @Schema(description = "Thông tin chủ sở hữu")
        UserResponse user,

        @Schema(description = "Slug URL", example = "pho-co")
        String slug,

        @Schema(description = "Địa chỉ", example = "123 Đường Láng")
        String address,

        @Schema(description = "Vĩ độ", example = "21.0285")
        Double latitude,

        @Schema(description = "Kinh độ", example = "105.8544")
        Double longitude,

        @Schema(description = "Số điện thoại", example = "0987654321")
        String phone,

        @Schema(description = "Trạng thái hoạt động", example = "true")
        boolean isActive,

        @Schema(description = "Điểm đánh giá trung bình", example = "4.5")
        Double ratingAverage,

        @Schema(description = "Số lượt đánh giá", example = "100")
        int reviewCount,

        @Schema(description = "Link ảnh đại diện", example = "https://cloud.../image.jpg")
        String imageFileUrl,

        @Schema(description = "Mô tả nhà hàng")
        String description
) implements Serializable {
}
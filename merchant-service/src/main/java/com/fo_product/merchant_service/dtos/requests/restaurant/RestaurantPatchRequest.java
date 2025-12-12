package com.fo_product.merchant_service.dtos.requests.restaurant;

import com.fo_product.common_lib.exceptions.validation.constraint.PhoneConstraint;
import io.swagger.v3.oas.annotations.media.Schema;

public record RestaurantPatchRequest(
        @Schema(description = "Tên mới", example = "Phở Cồ 2")
        String name,

        @Schema(description = "Mô tả mới", example = "Mở rộng thêm món cơm rang")
        String description,

        @Schema(description = "ID Chủ mới (nếu chuyển nhượng)", example = "102")
        Long ownerId,

        @Schema(description = "Slug mới", example = "pho-co-2")
        String slug,

        @Schema(description = "Địa chỉ mới", example = "456 Cầu Giấy")
        String address,

        @Schema(description = "Vĩ độ mới", example = "21.033")
        Double latitude,

        @Schema(description = "Kinh độ mới", example = "105.800")
        Double longitude,

        @Schema(description = "SĐT mới", example = "0912345678")
        @PhoneConstraint(length = 10, message = "INVALID_PHONE")
        String phone,

        @Schema(description = "Thay đổi trạng thái họat động hoặc ngừng hợp tác của nhà hàng với sàn thương mại FO", example = "false")
        Boolean isActive,

        @Schema(description = "Thay đổi trạng thái mở cửa/ đóng cửa tạm thời của chủ quán", example = "false")
        Boolean isOpen
) {
}
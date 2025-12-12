package com.fo_product.user_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PendingUserResponse(
        @Schema(description = "Email đang chờ xác thực", example = "nguyenvana01@gmail.com")
        String email,

        @Schema(description = "Tên", example = "Minh")
        String firstName,

        @Schema(description = "Họ", example = "Le")
        String lastName,

        @Schema(description = "Ngày sinh", example = "1999-12-30")
        LocalDate dob,

        @Schema(description = "Số điện thoại", example = "0912345678")
        String phone,

        @Schema(description = "Trạng thái tài khoản", example = "PENDING_VERIFICATION")
        String status
) {
}
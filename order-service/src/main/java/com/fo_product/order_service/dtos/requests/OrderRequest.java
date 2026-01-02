package com.fo_product.order_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Dữ liệu yêu cầu để tạo đơn hàng mới")
public record OrderRequest(

        @Schema(description = "ID của nhà hàng/quán ăn", example = "1")
        @NotNull(message = "NOT_NULL")
        Long merchantId,

        @Schema(description = "Phương thức thanh toán", example = "COD", allowableValues = {"COD", "BANKING", "VNPAY"})
        @NotNull(message = "NOT_NULL")
        String paymentMethod,

        @Schema(description = "Địa chỉ giao hàng chi tiết (Text)", example = "Số 144 Xuân Thủy, Cầu Giấy, Hà Nội")
        @NotBlank(message = "NOT_BLANK")
        String deliveryAddress,

        @Schema(description = "Vĩ độ giao hàng (Lấy từ GPS thiết bị)", example = "21.0366")
        @NotNull(message = "Tọa độ không được để trống")
        Double deliveryLatitude,

        @Schema(description = "Kinh độ giao hàng (Lấy từ GPS thiết bị)", example = "105.7820")
        @NotNull(message = "Tọa độ không được để trống")
        Double deliveryLongitude,

        @Schema(description = "Danh sách các món muốn đặt")
        @NotEmpty(message = "NOT_EMPTY")
        @Valid
        List<OrderItemRequest> items
) {
}
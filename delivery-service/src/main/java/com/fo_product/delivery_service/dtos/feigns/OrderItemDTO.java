package com.fo_product.delivery_service.dtos.feigns;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record OrderItemDTO(
        @Schema(description = "ID của chi tiết đơn hàng", example = "10")
        Long id,

        @Schema(description = "ID sản phẩm gốc (bên Merchant)", example = "105")
        Long productId,

        @Schema(description = "Tên sản phẩm tại thời điểm đặt", example = "Trà sữa chân trâu đường đen")
        String productName,

        @Schema(description = "Ảnh sản phẩm", example = "https://res.cloudinary.com/.../ts.jpg")
        String productImage,

        @Schema(description = "Đơn giá (Giá gốc + Giá sale nếu có)", example = "50000.00")
        BigDecimal unitPrice,

        @Schema(description = "Số lượng đặt", example = "2")
        Integer quantity,

        @Schema(description = "Tổng tiền cho món này (Đã bao gồm topping)", example = "110000.00")
        BigDecimal totalPrice,

        @Schema(description = "Danh sách các tùy chọn (Topping) đi kèm")
        List<OrderItemOptionDTO> options
) {
}

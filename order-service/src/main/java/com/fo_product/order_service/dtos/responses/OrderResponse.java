package com.fo_product.order_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
        @Schema(description = "ID đơn hàng", example = "1001")
        Long id,

        @Schema(description = "ID người đặt", example = "1")
        Long userId,

        // --- Thông tin Quán ---
        @Schema(description = "ID quán", example = "50")
        Long merchantId,

        @Schema(description = "Tên quán (Snapshot)", example = "Cơm Tấm Sài Gòn")
        String merchantName,

        @Schema(description = "Logo quán (Snapshot)", example = "https://.../logo.png")
        String merchantLogo,

        // --- Thông tin Người nhận ---
        @Schema(description = "Tên người nhận", example = "Nguyễn Văn A")
        String customerName,

        @Schema(description = "SĐT người nhận", example = "0987654321")
        String customerPhone,

        @Schema(description = "Địa chỉ giao hàng", example = "144 Xuân Thủy, Cầu Giấy")
        String deliveryAddress,

        // --- Tiền nong ---
        @Schema(description = "Tổng tiền món ăn (Chưa ship)", example = "150000.00")
        BigDecimal subTotal,

        @Schema(description = "Phí vận chuyển", example = "15000.00")
        BigDecimal shippingFee,

        @Schema(description = "Số tiền được giảm", example = "0.00")
        BigDecimal discountAmount,

        @Schema(description = "Tổng tiền phải thanh toán", example = "165000.00")
        BigDecimal grandTotal,

        // --- Trạng thái & Khác ---
        @Schema(description = "Ghi chú đơn hàng", example = "Không lấy tương ớt")
        String descriptionOrder,

        @Schema(description = "Trạng thái đơn hàng", example = "PENDING")
        String orderStatus, // Trả về String của Enum

        @Schema(description = "Phương thức thanh toán", example = "COD")
        String paymentMethod, // Trả về String của Enum

        @Schema(description = "Thời gian tạo đơn", example = "2024-01-01T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Thời gian cập nhật lần cuối", example = "2024-01-01T12:05:00")
        LocalDateTime updatedAt,

        @Schema(description = "Danh sách món ăn trong đơn")
        List<OrderItemResponse> orderItems
) {
}

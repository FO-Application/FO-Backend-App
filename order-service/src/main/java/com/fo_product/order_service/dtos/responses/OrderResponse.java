package com.fo_product.order_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "Đối tượng phản hồi chi tiết đơn hàng (Bao gồm thông tin vị trí và giá ship)")
public record OrderResponse(
        @Schema(description = "ID định danh của đơn hàng", example = "1001")
        Long id,

        @Schema(description = "ID của người dùng đặt hàng", example = "505")
        Long userId,

        // --- Thông tin Quán (Snapshot) ---
        @Schema(description = "ID của nhà hàng/quán ăn", example = "20")
        Long merchantId,

        @Schema(description = "Tên quán tại thời điểm đặt (Snapshot)", example = "Cơm Tấm Sài Gòn")
        String merchantName,

        @Schema(description = "Logo quán tại thời điểm đặt (Snapshot)", example = "https://minio.host/logo.png")
        String merchantLogo,

        @Schema(description = "Vĩ độ của quán (Dùng để vẽ map)", example = "21.028511")
        Double merchantLatitude,

        @Schema(description = "Kinh độ của quán (Dùng để vẽ map)", example = "105.854444")
        Double merchantLongitude,

        // --- Thông tin Người nhận & Giao hàng ---
        @Schema(description = "Tên người nhận hàng", example = "Nguyễn Văn A")
        String customerName,

        @Schema(description = "Số điện thoại người nhận", example = "0987654321")
        String customerPhone,

        @Schema(description = "Email người nhận", example = "khachhang@email.com")
        String customerEmail,

        @Schema(description = "Địa chỉ giao hàng (Dạng text hiển thị)", example = "144 Xuân Thủy, Cầu Giấy, Hà Nội")
        String deliveryAddress,

        @Schema(description = "Vĩ độ giao hàng (GPS người dùng)", example = "21.036662")
        Double deliveryLatitude,

        @Schema(description = "Kinh độ giao hàng (GPS người dùng)", example = "105.782061")
        Double deliveryLongitude,

        @Schema(description = "Khoảng cách vận chuyển ước tính (km)", example = "3.5")
        Double distanceKm,

        // --- Tiền nong ---
        @Schema(description = "Tổng tiền món ăn (Chưa bao gồm ship)", example = "150000.00")
        BigDecimal subTotal,

        @Schema(description = "Phí vận chuyển (Tính theo khoảng cách)", example = "15000.00")
        BigDecimal shippingFee,

        @Schema(description = "Số tiền được giảm giá", example = "0.00")
        BigDecimal discountAmount,

        @Schema(description = "Tổng tiền khách phải thanh toán (Grand Total)", example = "165000.00")
        BigDecimal grandTotal,

        // --- Trạng thái & Khác ---
        @Schema(description = "Ghi chú của khách hàng cho quán", example = "Không lấy tương ớt, nhiều cơm")
        String descriptionOrder,

        @Schema(description = "Trạng thái đơn hàng", example = "PREPARING")
        String orderStatus,

        @Schema(description = "Phương thức thanh toán", example = "COD")
        String paymentMethod,

        @Schema(description = "Thời gian tạo đơn", example = "2024-01-01T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Thời gian cập nhật trạng thái lần cuối", example = "2024-01-01T12:05:00")
        LocalDateTime updatedAt,

        @Schema(description = "Danh sách chi tiết các món ăn trong đơn")
        List<OrderItemResponse> orderItems,

        @Schema(description = "Thông tin đánh giá (Sẽ null nếu đơn chưa hoàn thành hoặc chưa đánh giá)")
        ReviewResponse review
) {
}
package com.fo_product.order_service.utils;

import java.math.BigDecimal;

public class DistanceCalculator {

    // Bán kính trái đất (km)
    private static final int EARTH_RADIUS = 6371;

    // Hệ số đường vòng (Winding Factor): Đường thực tế thường dài hơn đường chim bay khoảng 30%
    private static final double WINDING_FACTOR = 1.3;

    /**
     * Tính khoảng cách giữa 2 tọa độ GPS
     * Sử dụng công thức Haversine (Đường chim bay trên mặt cầu)
     * Sau đó nhân với hệ số đường vòng để ra ước lượng thực tế.
     *
     * @param lat1 Vĩ độ điểm A
     * @param lon1 Kinh độ điểm A
     * @param lat2 Vĩ độ điểm B
     * @param lon2 Kinh độ điểm B
     * @return Khoảng cách (km) đã làm tròn 1 chữ số thập phân (VD: 3.5)
     */
    public static double calculateEstimatedDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 0.0;
        }

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Khoảng cách đường chim bay
        double airDistance = EARTH_RADIUS * c;

        // Khoảng cách ước lượng thực tế (có đường vòng)
        double estimatedDistance = airDistance * WINDING_FACTOR;

        // Làm tròn 1 chữ số thập phân (VD: 3.456 -> 3.5)
        return Math.round(estimatedDistance * 10.0) / 10.0;
    }

    /**
     * Tính tiền ship dựa trên khoảng cách (km)
     * Chính sách giá:
     * - Dưới 2km: Giá cố định 15.000đ
     * - Trên 2km: Mỗi km tiếp theo cộng thêm 5.000đ
     */
    public static BigDecimal calculateShippingFee(double distanceKm) {
        // Giá mở cửa (Base fee) cho 2km đầu
        BigDecimal baseFee = BigDecimal.valueOf(15000);
        double baseDistance = 2.0;

        // Giá cho mỗi km tiếp theo
        BigDecimal pricePerKm = BigDecimal.valueOf(5000);

        if (distanceKm <= baseDistance) {
            return baseFee;
        } else {
            // Tính số km dư ra (VD: đi 3.5km thì dư 1.5km)
            double extraKm = distanceKm - baseDistance;

            // Tính tiền phần dư
            BigDecimal extraFee = pricePerKm.multiply(BigDecimal.valueOf(extraKm));

            // Tổng = Giá gốc + Giá dư
            // Làm tròn tiền về hàng trăm (VD: 22500 -> 23000 hoặc để nguyên tùy bạn)
            return baseFee.add(extraFee);
        }
    }
}
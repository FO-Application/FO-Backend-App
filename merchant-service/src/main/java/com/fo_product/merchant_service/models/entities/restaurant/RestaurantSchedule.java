package com.fo_product.merchant_service.models.entities.restaurant;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

/**
 * <pre>
 * ============================================================================
 * ENTITY: RestaurantSchedule
 * ============================================================================
 * Cấu hình khung giờ hoạt động của quán theo từng thứ trong tuần.
 * Hệ thống dùng bảng này để tự động tính toán trạng thái Đóng/Mở cửa.
 *
 * -----------------------
 * CHI TIẾT CÁC TRƯỜNG
 * -----------------------
 * 1. dayOfWeek:
 *    -> Giá trị số đại diện cho thứ trong tuần.
 *    -> (Convention: 1 = Thứ 2, ... 7 = Chủ Nhật - tùy theo logic Java Time).
 *
 * 2. openTime: Giờ bắt đầu mở cửa (VD: 08:00:00).
 *
 * 3. closeTime: Giờ đóng cửa (VD: 22:00:00).
 *
 * 4. restaurant (FK): Lịch này thuộc về nhà hàng nào.
 * ============================================================================
 * </pre>
 */
@Entity
@Table(name = "restaurant_schedules")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "day_of_week")
    int dayOfWeek;

    @Column(name = "open_time")
    LocalTime openTime;

    @Column(name = "close_time")
    LocalTime closeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    Restaurant restaurant;
}

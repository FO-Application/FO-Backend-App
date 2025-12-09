package com.fo_product.merchant_service.models.entities.restaurant;

import com.fo_product.merchant_service.models.entities.product.Category;
import com.fo_product.merchant_service.models.entities.product.Food;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * ============================================================================
 * ENTITY: Restaurant
 * ============================================================================
 * Thông tin chi tiết về Cửa hàng / Quán ăn đối tác.
 *
 * -----------------------
 * CHI TIẾT CÁC TRƯỜNG
 * -----------------------
 * 1. slug: URL thân thiện để SEO và share link (VD: com-tam-sai-gon-hao-nam).
 *
 * 2. latitude / longitude: Tọa độ GPS để tính khoảng cách giao hàng.
 *
 * 3. isActive:
 *    -> [TRUE]: Quán đang hợp tác hoạt động bình thường.
 *    -> [FALSE]: Quán bị khóa hoặc ngừng hợp tác.
 *
 * 4. isOpen:
 *    -> Trạng thái đóng/mở cửa Real-time (do chủ quán gạt nút hoặc auto theo lịch).
 *
 * 5. ratingAverage: Điểm đánh giá trung bình (VD: 4.8).
 *
 * 6. reviewCount: Tổng số lượng đánh giá.
 * ============================================================================
 * </pre>
 */
@Entity
@Table(name = "restaurants")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    Long ownerId;

    //Slug: URL thân thiện (VD: com-tam-sai-gon-hao-nam) - dùng để SEO và share link.
    @Column(nullable = false, unique = true)
    String slug;
    String address;

    Double latitude;
    Double longitude;

    String phone;

    @Column(name = "cover_image_url")
    String coverImageUrl;

    @Column(name = "is_active")
    boolean isActive;

    @Column(name = "is_open")
    boolean isOpen;

    @Column(name = "rating_average")
    Double ratingAverage;

    @Column(name = "review_count")
    int reviewCount;

    @Column(name = "image_file_name")
    String imageFileName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "restaurant_cuisine",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "cuisine_id")
    )
    List<Cuisine> cuisines = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    List<RestaurantSchedule> restaurantSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    List<Food> foods;
}

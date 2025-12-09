package com.fo_product.merchant_service.models.entities.product;

import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * ============================================================================
 * ENTITY: Category
 * ============================================================================
 * Phân loại thực đơn trong nhà hàng (VD: Món khai vị, Đồ uống, Món chính).
 *
 * -----------------------
 * CHI TIẾT CÁC TRƯỜNG
 * -----------------------
 * 1. displayOrder:
 *    -> Thứ tự hiển thị trên Menu (Số nhỏ hiện trước).
 *
 * 2. isActive: Ẩn/Hiện danh mục này trên ứng dụng client.
 *
 * 3. restaurant (FK): Nhà hàng sở hữu danh mục này.
 * ============================================================================
 * </pre>
 */
@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(name = "display_order")
    int displayOrder;

    @Column(name = "is_active")
    boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    Restaurant restaurant;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    List<Product> products;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    List<Food> foods;
}

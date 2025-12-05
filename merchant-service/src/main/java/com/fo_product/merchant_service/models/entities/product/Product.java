package com.fo_product.merchant_service.models.entities.product;

import com.fo_product.merchant_service.models.entities.addon.OptionGroup;
import com.fo_product.merchant_service.models.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * <pre>
 * ============================================================================
 * ENTITY: Product
 * ============================================================================
 * Đại diện cho món ăn hoặc đồ uống cụ thể được bán.
 *
 * -----------------------
 * CHI TIẾT CÁC TRƯỜNG
 * -----------------------
 * 1. price: Giá bán thực tế hiện tại.
 *
 * 2. originalPrice:
 *    -> Giá gốc trước khi giảm (dùng để gạch ngang giá: 50k -> 30k).
 *    -> Có thể null nếu không có giảm giá.
 *
 * 3. status: Trạng thái kinh doanh (VD: ACTIVE, OUT_OF_STOCK, INACTIVE).
 *
 * 4. category (FK): Món này thuộc danh mục nào.
 * ============================================================================
 * </pre>
 */
@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    Long price;

    @Column(name = "original_price")
    Long originalPrice;

    @Column(name = "image_url")
    String imageUrl;

    @Enumerated(EnumType.STRING)
    ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OptionGroup> optionGroups;
}

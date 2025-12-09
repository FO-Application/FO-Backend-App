package com.fo_product.merchant_service.models.entities.restaurant;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * <pre>
 * ============================================================================
 * ENTITY: Cuisine
 * ============================================================================
 * Các thẻ (tag) loại hình ẩm thực (VD: Cơm tấm, Trà sữa, Fast Food).
 * Dùng để phân loại, lọc và tìm kiếm nhà hàng trên hệ thống.
 *
 * -----------------------
 * CHI TIẾT CÁC TRƯỜNG
 * -----------------------
 * 1. name: Tên hiển thị loại hình (VD: "Món Hàn Quốc").
 *
 * 2. slug:
 *    -> URL thân thiện dùng để SEO hoặc tạo link filter.
 *    -> (VD: "mon-han-quoc", "tra-sua").
 * ============================================================================
 * </pre>
 */
@Entity
@Table(name = "cuisines")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cuisine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(nullable = false, unique = true)
    String slug;

    @Column(name = "image_file_name")
    String imageFileName;
}

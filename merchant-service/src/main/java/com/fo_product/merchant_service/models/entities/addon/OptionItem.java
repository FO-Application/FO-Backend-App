package com.fo_product.merchant_service.models.entities.addon;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * <pre>
 * ============================================================================
 * ENTITY: OptionItem
 * ============================================================================
 * Đại diện cho từng lựa chọn cụ thể nằm trong một OptionGroup.
 * (VD: Nếu Group là "Size", thì Item là "Size M", "Size L").
 *
 * -----------------------
 * CHI TIẾT CÁC TRƯỜNG
 * -----------------------
 * 1. name: Tên lựa chọn (VD: "Size L", "Trân châu đen").
 *
 * 2. priceAdjustment:
 *    -> Số tiền cộng thêm vào giá gốc món ăn khi chọn item này.
 *    -> Nếu không tính thêm tiền thì để là 0.
 *
 * 3. isAvailable: Trạng thái còn hàng/hết hàng của riêng option này.
 *
 * 4. optionGroup (FK): Nhóm tùy chọn cha chứa item này.
 * ============================================================================
 * </pre>
 */
@Entity
@Table(name = "option_items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(name = "price_adjustment")
    Long priceAdjustment;

    @Column(name = "is_available")
    boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id")
    OptionGroup optionGroup;
}

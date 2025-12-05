package com.fo_product.merchant_service.models.entities.addon;

import com.fo_product.merchant_service.models.entities.product.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * <pre>
 * ============================================================================
 * ENTITY: OptionGroup
 * ============================================================================
 * Đại diện cho một nhóm các tùy chọn đi kèm món ăn (VD: Nhóm Size, Nhóm Topping).
 * Quản lý các quy tắc chọn (bắt buộc hay không, chọn tối đa bao nhiêu).
 *
 * -----------------------
 * CHI TIẾT CÁC TRƯỜNG
 * -----------------------
 * 1. id (PK): Khóa chính tự tăng.
 *
 * 2. name: Tên hiển thị của nhóm (VD: "Chọn Size", "Thêm Topping").
 *
 * 3. isMandatory (Quan trọng):
 *    -> Xác định khách có bắt buộc phải chọn nhóm này không.
 *       + [TRUE] : Bắt buộc chọn ít nhất 1 cái (VD: Phải chọn Size).
 *       + [FALSE]: Không bắt buộc, có thể bỏ qua (VD: Topping).
 *
 * 4. minSelection / maxSelection:
 *    -> Số lượng mục tối thiểu và tối đa khách được phép chọn trong nhóm này.
 *
 * 5. product (FK): Món ăn sở hữu nhóm tùy chọn này.
 * ============================================================================
 * </pre>
 */
@Entity
@Table(name = "option_groups")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(name = "is_mandatory")
    boolean isMandatory;

    @Column(name = "min_selection")
    int minSelection;

    @Column(name = "max_selection")
    int maxSelection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL)
    List<OptionItem> optionItems;
}

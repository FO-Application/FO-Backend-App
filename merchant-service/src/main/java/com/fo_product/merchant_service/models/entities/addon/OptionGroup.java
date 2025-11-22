package com.fo_product.merchant_service.models.entities.addon;

import com.fo_product.merchant_service.models.entities.product.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
    private Product product;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL)
    private List<OptionItem> optionItems;
}

package com.fo_product.merchant_service.models.entities.product;

import com.fo_product.merchant_service.models.entities.addon.OptionGroup;
import com.fo_product.merchant_service.models.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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

package com.fo_product.order_service.models.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "product_id")
    Long productId;

    @Column(name = "product_name")
    String productName;

    @Column(name = "product_image")
    String productImage;

    @Column(name = "unit_price", precision = 19, scale = 2)
    BigDecimal unitPrice;

    Integer quantity;

    @Column(name = "total_price", precision = 19, scale = 2)
    BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    List<OrderItemOption> orderItemOptions = new ArrayList<>();
}

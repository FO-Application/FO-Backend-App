package com.fo_product.order_service.models.entities;

import com.fo_product.order_service.models.enums.OrderStatus;
import com.fo_product.order_service.models.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "merchant_id", nullable = false)
    Long merchantId;

    @Column(name = "merchant_name")
    String merchantName;

    @Column(name = "merchant_logo")
    String merchantLogo;

    @Column(name = "customer_name")
    String customerName;

    @Column(name = "customer_phone")
    String customerPhone;

    @Column(name = "customer_email")
    String customerEmail;

    @Column(name = "delivery_address")
    String deliveryAddress;

    @Column(name = "sub_total", precision = 19, scale = 2)
    BigDecimal subTotal;

    @Column(name = "shipping_fee",  precision = 19, scale = 2)
    BigDecimal shippingFee;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    BigDecimal discountAmount;

    @Column(name = "grand_total", precision = 19, scale = 2)
    BigDecimal grandTotal;

    @Column(name = "description_order", columnDefinition = "TEXT")
    String descriptionOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    PaymentMethod  paymentMethod;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade =  CascadeType.ALL, fetch = FetchType.LAZY)
    Review review;
}

package com.fo_product.delivery_service.models.entities;

import com.fo_product.delivery_service.models.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long orderId;

    @Column(name = "pickup_location")
    String pickupLocation;

    @Column(name = "delivery_location")
    String deliveryLocation;

    @Column(name = "customer_name")
    String customerName;

    @Column(name = "customer_phone")
    String customerPhone;

    @Enumerated(EnumType.STRING)
    DeliveryStatus status;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id")
    Shipper shipper;
}

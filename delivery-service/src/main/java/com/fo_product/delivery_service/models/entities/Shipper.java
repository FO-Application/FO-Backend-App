package com.fo_product.delivery_service.models.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shippers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shipper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    Long userId;

    @Column(name = "vehicle_number")
    String vehicleNumber;

    @Column(name = "vehicle_type")
    String vehicleType;

    @Builder.Default
    @Column(name = "is_online")
    boolean isOnline = false;

    @Builder.Default
    @Column(name = "is_available")
    boolean isAvailable = true;

    @OneToMany(mappedBy = "shipper", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Delivery> deliveries = new ArrayList<>();
}

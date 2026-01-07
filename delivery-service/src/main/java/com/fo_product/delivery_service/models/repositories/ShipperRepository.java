package com.fo_product.delivery_service.models.repositories;

import com.fo_product.delivery_service.models.entities.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    @Query(value = "SELECT * FROM shippers s WHERE s.is_online AND s.is_available = true ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Shipper> findRandomAvailableShipper();

    Optional<Shipper> findByUserId(Long userId);
}

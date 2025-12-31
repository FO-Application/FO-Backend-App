package com.fo_product.delivery_service.models.repositories;

import com.fo_product.delivery_service.models.entities.ShipperTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipperTransactionRepository extends JpaRepository<ShipperTransaction, Long> {
}

package com.fo_product.delivery_service.models.repositories;

import com.fo_product.delivery_service.models.entities.ShipperWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipperWalletRepository extends JpaRepository<ShipperWallet, Long> {
    Optional<ShipperWallet> findByShipper_Id(Long shipperId);
}

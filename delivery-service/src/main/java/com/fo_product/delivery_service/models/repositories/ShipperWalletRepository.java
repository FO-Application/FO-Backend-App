package com.fo_product.delivery_service.models.repositories;

import com.fo_product.delivery_service.models.entities.ShipperWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ShipperWalletRepository extends JpaRepository<ShipperWallet, Long> {
    Optional<ShipperWallet> findByShipper_Id(Long shipperId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sw FROM ShipperWallet sw WHERE sw.shipper.id = :shipperId")
    Optional<ShipperWallet> findLockedByShipper_Id(@Param("shipperId") Long shipperId);
}

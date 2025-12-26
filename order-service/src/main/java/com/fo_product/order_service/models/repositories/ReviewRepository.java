package com.fo_product.order_service.models.repositories;

import com.fo_product.order_service.models.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByOrder_Id(Long orderId);

    Page<Review> findByMerchantId(Long merchantId, Pageable pageable);

    Optional<Review> findByOrder_Id(Long orderId);
}

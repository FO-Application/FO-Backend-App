package com.fo_product.order_service.models.repositories;

import com.fo_product.order_service.models.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fo_product.order_service.models.enums.OrderStatus;

import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByMerchantId(Long merchantId, Pageable pageable);
    Optional<Order> findByAppTransId(String appTransId);
    //Lấy đơn của quán theo trạng thái cụ thể
    Page<Order> findByMerchantIdAndOrderStatus(Long merchantId, OrderStatus orderStatus, Pageable pageable);

    long countByMerchantIdAndCreatedAtBetween(Long merchantId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.grandTotal) FROM Order o WHERE o.merchantId = :merchantId AND o.orderStatus = 'COMPLETED'")
    java.math.BigDecimal sumGrandTotalByMerchantIdAndStatusCompleted(Long merchantId);
}

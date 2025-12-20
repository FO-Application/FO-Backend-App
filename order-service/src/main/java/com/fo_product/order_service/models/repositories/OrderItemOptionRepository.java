package com.fo_product.order_service.models.repositories;

import com.fo_product.order_service.models.entities.OrderItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemOptionRepository extends JpaRepository<OrderItemOption, Long> {
}

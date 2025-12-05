package com.fo_product.merchant_service.models.repositories.restaurant;

import com.fo_product.merchant_service.models.entities.restaurant.RestaurantSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantScheduleRepository extends JpaRepository<RestaurantSchedule, Long> {
}

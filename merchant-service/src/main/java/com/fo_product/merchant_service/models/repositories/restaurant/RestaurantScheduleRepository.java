package com.fo_product.merchant_service.models.repositories.restaurant;

import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.entities.restaurant.RestaurantSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantScheduleRepository extends JpaRepository<RestaurantSchedule, Long> {
    List<RestaurantSchedule> findAllByRestaurant(Restaurant restaurant);
    boolean existsByRestaurantAndDayOfWeek(Restaurant restaurant, int dayOfWeek);
}

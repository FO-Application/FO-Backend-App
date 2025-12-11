package com.fo_product.merchant_service.models.repositories.restaurant;

import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}

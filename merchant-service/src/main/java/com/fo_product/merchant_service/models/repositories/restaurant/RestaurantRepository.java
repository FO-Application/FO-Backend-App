package com.fo_product.merchant_service.models.repositories.restaurant;

import com.fo_product.merchant_service.models.entities.restaurant.Cuisine;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    Optional<Restaurant> findBySlug(String slug);
    Page<Restaurant> findByCuisinesContaining(Cuisine cuisine, Pageable pageable);
}

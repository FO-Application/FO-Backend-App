package com.fo_product.merchant_service.models.repositories.product;

import com.fo_product.merchant_service.models.entities.product.Category;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByRestaurantOrderByDisplayOrderAsc(Restaurant restaurant);
    boolean existsByNameAndRestaurant(String name, Restaurant restaurant);
}

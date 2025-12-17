package com.fo_product.merchant_service.models.repositories.product;

import com.fo_product.merchant_service.models.entities.product.Category;
import com.fo_product.merchant_service.models.entities.product.Product;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameAndCategory_Restaurant(String name, Restaurant categoryRestaurant);
    List<Product> findAllByCategory(Category category);
}

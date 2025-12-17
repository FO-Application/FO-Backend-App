package com.fo_product.merchant_service.models.repositories.restaurant;

import com.fo_product.merchant_service.models.entities.restaurant.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuisineRepository extends JpaRepository<Cuisine, Long> {
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
    Optional<Cuisine> findBySlug(String slug);
}

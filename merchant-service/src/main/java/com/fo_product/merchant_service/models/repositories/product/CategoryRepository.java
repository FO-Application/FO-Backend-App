package com.fo_product.merchant_service.models.repositories.product;

import com.fo_product.merchant_service.models.entities.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}

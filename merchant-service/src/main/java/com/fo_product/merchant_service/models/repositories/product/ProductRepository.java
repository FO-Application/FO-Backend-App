package com.fo_product.merchant_service.models.repositories.product;

import com.fo_product.merchant_service.models.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}

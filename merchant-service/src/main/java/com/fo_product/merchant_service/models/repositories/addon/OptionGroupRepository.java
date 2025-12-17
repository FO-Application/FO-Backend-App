package com.fo_product.merchant_service.models.repositories.addon;

import com.fo_product.merchant_service.models.entities.addon.OptionGroup;
import com.fo_product.merchant_service.models.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionGroupRepository extends JpaRepository<OptionGroup, Long> {
    List<OptionGroup> findAllByProduct(Product product);
    boolean existsByNameAndProduct(String name, Product product);
}

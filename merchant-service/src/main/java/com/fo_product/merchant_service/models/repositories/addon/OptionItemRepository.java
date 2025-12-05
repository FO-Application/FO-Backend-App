package com.fo_product.merchant_service.models.repositories.addon;

import com.fo_product.merchant_service.models.entities.addon.OptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionItemRepository extends JpaRepository<OptionItem, Long> {
}

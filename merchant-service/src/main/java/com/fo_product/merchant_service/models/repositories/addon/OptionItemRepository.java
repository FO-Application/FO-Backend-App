package com.fo_product.merchant_service.models.repositories.addon;

import com.fo_product.merchant_service.models.entities.addon.OptionGroup;
import com.fo_product.merchant_service.models.entities.addon.OptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionItemRepository extends JpaRepository<OptionItem, Long> {
    List<OptionItem> findAllByOptionGroup(OptionGroup optionGroup);
    boolean existsByNameAndOptionGroup(String name, OptionGroup optionGroup);
}

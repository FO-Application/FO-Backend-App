package com.fo_product.merchant_service.models.repositories.addon;

import com.fo_product.merchant_service.models.entities.addon.OptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionGroupRepository extends JpaRepository<OptionGroup, Long> {
}

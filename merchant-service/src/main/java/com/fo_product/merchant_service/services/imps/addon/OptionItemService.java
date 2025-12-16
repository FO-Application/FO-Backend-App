package com.fo_product.merchant_service.services.imps.addon;

import com.fo_product.merchant_service.dtos.requests.option_item.OptionItemPatchRequest;
import com.fo_product.merchant_service.dtos.requests.option_item.OptionItemRequest;
import com.fo_product.merchant_service.dtos.responses.OptionItemResponse;
import com.fo_product.merchant_service.mappers.OptionItemMapper;
import com.fo_product.merchant_service.models.repositories.addon.OptionGroupRepository;
import com.fo_product.merchant_service.models.repositories.addon.OptionItemRepository;
import com.fo_product.merchant_service.services.interfaces.addon.IOptionItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OptionItemService implements IOptionItemService {
    OptionItemRepository optionItemRepository;
    OptionGroupRepository optionGroupRepository;
    OptionItemMapper optionItemMapper;

    @Override
    @Transactional
    @CacheEvict(value = "cacheOptionItems", allEntries = true)
    public OptionItemResponse createOptionItem(OptionItemRequest optionItemRequest) {



        return null;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "cacheOptionItems", allEntries = true),
                    @CacheEvict(value = "optionItem_details", key = "#id")
            }
    )
    public OptionItemResponse updateOptionItem(Long id, OptionItemPatchRequest optionItemRequest) {
        return null;
    }

    @Override
    @Cacheable(value = "optionItem_details", key = "#id")
    public OptionItemResponse getOptionItem(Long id) {
        return null;
    }

    @Override
    @Cacheable(value = "cacheOptionItems")
    public List<OptionItemResponse> getOptionItems() {
        return null;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "cacheOptionItems", allEntries = true),
                    @CacheEvict(value = "optionItem_details", key = "#id")
            }
    )
    public void deleteOptionItem(Long id) {

    }
}

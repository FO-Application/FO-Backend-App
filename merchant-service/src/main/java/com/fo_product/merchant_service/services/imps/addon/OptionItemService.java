package com.fo_product.merchant_service.services.imps.addon;

import com.fo_product.merchant_service.dtos.requests.option_item.OptionItemPatchRequest;
import com.fo_product.merchant_service.dtos.requests.option_item.OptionItemRequest;
import com.fo_product.merchant_service.dtos.responses.OptionItemResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;
import com.fo_product.merchant_service.mappers.OptionItemMapper;
import com.fo_product.merchant_service.models.entities.addon.OptionGroup;
import com.fo_product.merchant_service.models.entities.addon.OptionItem;
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
    public OptionItemResponse createOptionItem(OptionItemRequest request) {
        OptionGroup optionGroup = optionGroupRepository.findById(request.optionGroupId())
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.OPTION_GROUP_NOT_EXIST));

        if (optionItemRepository.existsByNameAndOptionGroup(request.name(), optionGroup)) {
            throw new MerchantException(MerchantExceptionCode.OPTION_ITEM_EXIST);
        }

        OptionItem optionItem = OptionItem.builder()
                .name(request.name())
                .priceAdjustment(request.priceAdjustment())
                .isAvailable(true)
                .optionGroup(optionGroup)
                .build();

        OptionItem result = optionItemRepository.save(optionItem);

        return optionItemMapper.response(result);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "cacheOptionItems", allEntries = true),
                    @CacheEvict(value = "optionItem_details", key = "#id")
            }
    )
    public OptionItemResponse updateOptionItem(Long id, OptionItemPatchRequest request) {
        OptionItem optionItem = optionItemRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.OPTION_ITEM_NOT_EXIST));

        if (request.name() != null && !request.name().equals(optionItem.getName())) {
            if (optionItemRepository.existsByNameAndOptionGroup(request.name(), optionItem.getOptionGroup())) {
                throw new MerchantException(MerchantExceptionCode.OPTION_ITEM_EXIST);
            }
            optionItem.setName(request.name());
        }

        if (request.priceAdjustment() != null)
            optionItem.setPriceAdjustment(request.priceAdjustment());

        if (request.isAvailable() != null)
            optionItem.setAvailable(request.isAvailable());

        OptionItem result = optionItemRepository.save(optionItem);
        return optionItemMapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "optionItem_details", key = "#id")
    public OptionItemResponse getOptionItem(Long id) {
        OptionItem optionItem = optionItemRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.OPTION_ITEM_NOT_EXIST));

        return optionItemMapper.response(optionItem);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cacheOptionItems", key = "#groupId")
    public List<OptionItemResponse> getOptionItemsByGroupId(Long groupId) {
        OptionGroup optionGroup = optionGroupRepository.findById(groupId)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.OPTION_GROUP_NOT_EXIST));

        List<OptionItem> result = optionItemRepository.findAllByOptionGroup(optionGroup);

        return result.stream().map(optionItemMapper::response).toList();
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "cacheOptionItems", allEntries = true),
                    @CacheEvict(value = "optionItem_details", key = "#id")
            }
    )
    public void deleteOptionItem(Long id) {
        OptionItem optionItem = optionItemRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.OPTION_ITEM_NOT_EXIST));

        optionItemRepository.delete(optionItem);
    }
}
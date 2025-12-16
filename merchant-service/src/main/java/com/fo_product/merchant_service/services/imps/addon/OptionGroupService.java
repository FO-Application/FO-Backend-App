package com.fo_product.merchant_service.services.imps.addon;

import com.fo_product.merchant_service.dtos.requests.option_group.OptionGroupPatchRequest;
import com.fo_product.merchant_service.dtos.requests.option_group.OptionGroupRequest;
import com.fo_product.merchant_service.dtos.responses.OptionGroupResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;
import com.fo_product.merchant_service.mappers.OptionGroupMapper;
import com.fo_product.merchant_service.models.entities.addon.OptionGroup;
import com.fo_product.merchant_service.models.entities.product.Product;
import com.fo_product.merchant_service.models.repositories.addon.OptionGroupRepository;
import com.fo_product.merchant_service.models.repositories.product.ProductRepository;
import com.fo_product.merchant_service.services.interfaces.addon.IOptionGroupService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OptionGroupService implements IOptionGroupService {
    OptionGroupRepository optionGroupRepository;
    ProductRepository productRepository;
    OptionGroupMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = "cacheOptionGroups", allEntries = true)
    public OptionGroupResponse createOptionGroup(OptionGroupRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.PRODUCT_NOT_EXIST));

        OptionGroup optionGroup = OptionGroup.builder()
                .name(request.name())
                .isMandatory(request.isMandatory())
                .minSelection(request.minSelection())
                .maxSelection(request.maxSelection())
                .product(product)
                .build();

        OptionGroup result = optionGroupRepository.save(optionGroup);

        return mapper.response(result);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "cacheOptionGroups", allEntries = true),
                    @CacheEvict(value = "optionGroup_details", key = "#id")
            }
    )
    public OptionGroupResponse updateOptionGroup(Long id, OptionGroupPatchRequest request) {
        OptionGroup optionGroup = optionGroupRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.OPTION_GROUP_NOT_EXIST));

        if (request.name() != null)
            optionGroup.setName(request.name());

        if (request.isMandatory() != null)
            optionGroup.setMandatory(request.isMandatory());

        if (request.minSelection() != null)
            optionGroup.setMinSelection(request.minSelection());

        if (request.maxSelection() != null)
            optionGroup.setMaxSelection(request.maxSelection());

        OptionGroup result = optionGroupRepository.save(optionGroup);

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "optionGroup_details", key = "#id")
    public OptionGroupResponse getOptionGroup(Long id) {
        OptionGroup optionGroup = optionGroupRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.OPTION_GROUP_NOT_EXIST));

        return mapper.response(optionGroup);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cacheOptionGroups")
    public List<OptionGroupResponse> getOptionGroups() {
        List<OptionGroup> result = optionGroupRepository.findAll();

        return result.stream().map(mapper::response).toList();
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "cacheOptionGroups", allEntries = true),
                    @CacheEvict(value = "optionGroup_details", key = "#id")
            }
    )
    public void deleteOptionGroup(Long id) {
        OptionGroup optionGroup = optionGroupRepository.findById(id)
                .orElseThrow(() -> new MerchantException(MerchantExceptionCode.OPTION_GROUP_NOT_EXIST));

        optionGroupRepository.delete(optionGroup);
    }
}

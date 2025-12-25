package com.fo_product.merchant_service.services.imps.addon;

import com.fo_product.merchant_service.dtos.requests.option_group.OptionGroupPatchRequest;
import com.fo_product.merchant_service.dtos.requests.option_group.OptionGroupRequest;
import com.fo_product.merchant_service.dtos.responses.addon.OptionGroupResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantErrorCode;
import com.fo_product.merchant_service.mappers.addon.OptionGroupMapper;
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
    // Cache: Xóa theo productId để tối ưu (hoặc allEntries cũng tạm chấp nhận)
    @CacheEvict(value = "cacheOptionGroups", key = "#request.productId()")
    public OptionGroupResponse createOptionGroup(OptionGroupRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.PRODUCT_NOT_EXIST));

        // FIX 1: Check trùng theo Product
        if (optionGroupRepository.existsByNameAndProduct(request.name(), product)) {
            throw new MerchantException(MerchantErrorCode.OPTION_GROUP_EXIST);
        }

        if (!validateSelection(request.minSelection(), request.maxSelection())) {
            throw new MerchantException(MerchantErrorCode.INVALID_SELECTION_RANGE);
        }

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
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.OPTION_GROUP_NOT_EXIST));

        // Check trùng tên khi update
        if (request.name() != null && !request.name().equals(optionGroup.getName())) {
            // FIX: Check trùng theo Product
            if (optionGroupRepository.existsByNameAndProduct(request.name(), optionGroup.getProduct())) {
                throw new MerchantException(MerchantErrorCode.OPTION_GROUP_EXIST);
            }
            optionGroup.setName(request.name());
        }

        Integer newMin = request.minSelection() != null ? request.minSelection() : optionGroup.getMinSelection();
        Integer newMax = request.maxSelection() != null ? request.maxSelection() : optionGroup.getMaxSelection();

        if (!validateSelection(newMin, newMax)) {
            throw new MerchantException(MerchantErrorCode.INVALID_SELECTION_RANGE);
        }

        // FIX 3: Xóa đoạn set Name thừa thãi ở đây đi

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
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.OPTION_GROUP_NOT_EXIST));

        return mapper.response(optionGroup);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cacheOptionGroups", key = "#productId")
    public List<OptionGroupResponse> getOptionGroupsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.PRODUCT_NOT_EXIST));

        List<OptionGroup> result = optionGroupRepository.findAllByProduct(product);

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
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.OPTION_GROUP_NOT_EXIST));

        optionGroupRepository.delete(optionGroup);
    }

    private boolean validateSelection(Integer min, Integer max) {
        if (min != null && max != null) {
            if (min > max) return false;
            if (min < 0) return false;
            if (max < 0) return false;
        }
        return true;
    }
}
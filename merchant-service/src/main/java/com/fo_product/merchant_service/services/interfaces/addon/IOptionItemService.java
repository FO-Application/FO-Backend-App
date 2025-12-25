package com.fo_product.merchant_service.services.interfaces.addon;

import com.fo_product.merchant_service.dtos.requests.option_item.OptionItemPatchRequest;
import com.fo_product.merchant_service.dtos.requests.option_item.OptionItemRequest;
import com.fo_product.merchant_service.dtos.responses.addon.OptionItemResponse;

import java.util.List;

public interface IOptionItemService {
    OptionItemResponse createOptionItem(OptionItemRequest optionItemRequest);
    OptionItemResponse updateOptionItem(Long id, OptionItemPatchRequest optionItemRequest);
    OptionItemResponse getOptionItem(Long id);
    List<OptionItemResponse> getOptionItemsByGroupId(Long groupId);
    void deleteOptionItem(Long id);
}

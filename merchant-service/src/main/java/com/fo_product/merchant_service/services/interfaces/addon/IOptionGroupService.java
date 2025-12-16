package com.fo_product.merchant_service.services.interfaces.addon;

import com.fo_product.merchant_service.dtos.requests.option_group.OptionGroupPatchRequest;
import com.fo_product.merchant_service.dtos.requests.option_group.OptionGroupRequest;
import com.fo_product.merchant_service.dtos.responses.OptionGroupResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IOptionGroupService {
    OptionGroupResponse createOptionGroup(OptionGroupRequest request);
    OptionGroupResponse updateOptionGroup(Long id, OptionGroupPatchRequest request);
    OptionGroupResponse getOptionGroup(Long id);
    List<OptionGroupResponse> getOptionGroups();
    void deleteOptionGroup(Long id);
}

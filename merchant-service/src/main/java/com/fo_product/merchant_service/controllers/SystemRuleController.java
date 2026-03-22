package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.SystemRulesDTO;
import com.fo_product.merchant_service.services.imps.SystemRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/system/rules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "System Rules", description = "Admin Config APIs")
public class SystemRuleController {
    SystemRuleService systemRuleService;

    @GetMapping
    public APIResponse<SystemRulesDTO> getRules() {
        return APIResponse.<SystemRulesDTO>builder()
                .result(systemRuleService.getRules())
                .message("Success")
                .build();
    }

    @PutMapping
    public APIResponse<SystemRulesDTO> updateRules(@RequestBody SystemRulesDTO rules) {
        return APIResponse.<SystemRulesDTO>builder()
                .result(systemRuleService.updateRules(rules))
                .message("Rules updated successfully")
                .build();
    }
}

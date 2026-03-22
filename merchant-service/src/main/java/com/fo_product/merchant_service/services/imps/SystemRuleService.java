package com.fo_product.merchant_service.services.imps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fo_product.merchant_service.dtos.SystemRulesDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class SystemRuleService {
    private final String FILE_PATH = "system-rules.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private SystemRulesDTO currentRules;

    @PostConstruct
    public void init() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                currentRules = objectMapper.readValue(file, SystemRulesDTO.class);
            } catch (IOException e) {
                log.error("Failed to read system rules JSON, using defaults", e);
                createDefaultRules();
            }
        } else {
            createDefaultRules();
        }
    }

    private void createDefaultRules() {
        currentRules = SystemRulesDTO.builder()
                .platformFeePercentage(10.0)
                .driverFeePercentage(20.0)
                .baseDeliveryFee(15000.0)
                .perKmFee(5000.0)
                .autoApproveRestaurant(false)
                .build();
        saveRules(currentRules);
    }

    public SystemRulesDTO getRules() {
        return currentRules;
    }

    public SystemRulesDTO updateRules(SystemRulesDTO newRules) {
        currentRules = newRules;
        saveRules(currentRules);
        return currentRules;
    }

    private void saveRules(SystemRulesDTO rules) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), rules);
        } catch (IOException e) {
            log.error("Failed to save system rules", e);
        }
    }
}

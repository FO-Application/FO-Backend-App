package com.fo_product.user_service.seeders;

import com.fo_product.user_service.models.entities.Role;
import com.fo_product.user_service.models.repositories.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Order(1)
public class RoleSeeder implements CommandLineRunner {
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Role seeder is running...");
        
        createRoleIfNotFound("CUSTOMER", "Represents the end-user seeking to purchase products and request delivery services.");
        createRoleIfNotFound("MERCHANT", "A role designed to administer business operations and serve customers on the application.");
        createRoleIfNotFound("SHIPPER", "A role designed to ship orders and serve customers on the application.");
        createRoleIfNotFound("SUPER_ADMIN", "System administrator with full access to all resources.");
        
        log.info("Role seeder completed!");
    }

    private void createRoleIfNotFound(String name, String description) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = Role.builder()
                    .name(name)
                    .description(description)
                    .build();
            roleRepository.save(role);
            log.info("Seeded missing role: {}", name);
        }
    }
}

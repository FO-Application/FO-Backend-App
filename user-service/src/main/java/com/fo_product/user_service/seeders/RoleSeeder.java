package com.fo_product.user_service.seeders;

import com.fo_product.user_service.models.entities.Role;
import com.fo_product.user_service.models.repositories.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleSeeder implements CommandLineRunner {
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Data seeder is running...");

        if (isTableEmpty()) {
            List<Role> roles = List.of(
                Role.builder()
                    .name("CUSTOMER")
                    .description("Represents the end-user seeking to purchase products and request delivery services.")
                    .build(),

                Role.builder()
                    .name("MERCHANT")
                    .description("A role designed to administer business operations and serve customers on the application.")
                    .build(),

                Role.builder()
                    .name("SHIPPER")
                    .description("A role designed to ship orders and serve customers on the application.")
                    .build()
            );
            roleRepository.saveAll(roles);
            log.info("All roles have been seeded successfully!");
        }
    }

    private boolean isTableEmpty() {
        return roleRepository.count() == 0;
    }
}

package com.fo_product.user_service.seeders;

import com.fo_product.user_service.models.entities.Role;
import com.fo_product.user_service.models.entities.User;
import com.fo_product.user_service.models.enums.AuthProvider;
import com.fo_product.user_service.models.repositories.RoleRepository;
import com.fo_product.user_service.models.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Order(2) // Runs after RoleSeeder
public class AdminSeeder implements CommandLineRunner {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Checking for default System Admin...");

        String adminEmail = "admin@fastmanager.com";
        Optional<User> adminOpt = userRepository.findByEmail(adminEmail);

        if (adminOpt.isEmpty()) {
            Role adminRole = roleRepository.findByName("SUPER_ADMIN")
                    .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found. Please check RoleSeeder."));

            User superAdmin = User.builder()
                    .email(adminEmail)
                    .firstName("Super")
                    .lastName("Admin")
                    .password(passwordEncoder.encode("123456"))
                    .phone("0123456789")
                    .role(adminRole)
                    .userStatus(true)
                    .authProvider(AuthProvider.LOCAL)
                    .build();

            userRepository.save(superAdmin);
            log.info("Default Super Admin created: email={}, password=123456", adminEmail);
        } else {
            log.info("Default Super Admin already exists.");
        }
    }
}

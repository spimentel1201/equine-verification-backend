package com.horsetrust.config;

import com.horsetrust.models.entities.User;
import com.horsetrust.models.enums.UserRole;
import com.horsetrust.models.enums.UserStatus;
import com.horsetrust.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedDatabase() {
        seedAdminUser();
    }

    private void seedAdminUser() {
        if (!userRepository.existsByRole(UserRole.ADMIN)) {
            User admin = User.builder()
                    .email("admin@horsetrust.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .firstName("Super")
                    .lastName("Admin")
                    .role(UserRole.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(admin);
            log.info("✅ Creado usuario administrador por defecto: admin@horsetrust.com / admin1234");
        } else {
            log.info("ℹ️ Ya existe al menos un usuario administrador en la BD.");
        }
    }
}

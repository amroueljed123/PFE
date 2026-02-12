package com.esb.appback.initialization;

import com.esb.appback.model.User;
import com.esb.appback.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner initializeData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create test admin user if it doesn't exist
            if (userRepository.findByEmail("admin@test.com").isEmpty()) {
                User adminUser = new User(
                        "Admin User",
                        "admin@test.com",
                        passwordEncoder.encode("admin123"),
                        "admin",
                        true
                );
                userRepository.save(adminUser);
                System.out.println("✓ Test admin user created: admin@test.com / admin123");
            }
        };
    }
}

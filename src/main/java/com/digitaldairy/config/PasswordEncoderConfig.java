package com.digitaldairy.config;

/**
 * PasswordEncoderConfig: Dedicated bean for BCrypt password hashing.
 * Used in AuthService for farmer reg (phone + userId + pass) and staff onboarding.
 * Strength=12 for balance (secure, not too slow for H2 dev).
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // Default strength=10; 12 for dairy-scale
    }
}
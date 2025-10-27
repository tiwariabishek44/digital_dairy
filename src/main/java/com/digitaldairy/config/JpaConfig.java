package com.digitaldairy.config;

/**
 * JpaConfig: Customizes JPA/Hibernate for auditing, naming strategies, and H2/PostgreSQL dialect switching.
 * Enables entity auditing for milk records (created/updated dates), snake_case column names for DB consistency.
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.ZonedDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@EnableJpaRepositories(basePackages = "com.digitaldairy.repository")
public class JpaConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now());
    }

    // Add custom naming strategy if needed (e.g., snake_case)
    // @Bean
    // public PhysicalNamingStrategy physicalNamingStrategy() {
    //     return new SnakeCasePhysicalNamingStrategy();
    // }
}
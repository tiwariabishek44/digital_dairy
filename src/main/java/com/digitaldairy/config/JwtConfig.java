package com.digitaldairy.config;

/**
 * JwtConfig: Provides configuration beans for JWT token generation and validation.
 * Loads secret and expiration times from application.properties. Used by JwtUtil for token handling.
 */

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Bean
    public SecretKey jwtSecretKey() {
        // Use HS512 for strong signing; secret should be at least 512 bits (64 chars base64)
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Getter for access token expiration (in ms).
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Getter for refresh token expiration (in ms).
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
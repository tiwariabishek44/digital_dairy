package com.digitaldairy.security;

/**
 * JwtUtil: Utility for generating, validating, and extracting claims from JWT tokens.
 * Supports access (short-lived) and refresh (long-lived) tokens.
 * Embeds userId, dairyCenterId, and roles in claims for multi-tenant auth.
 * Uses HS512 signing; handles expiration and invalid tokens.
 * Updated for JJWT 0.12.6+ syntax (parserBuilder).
 */

import com.digitaldairy.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        return jwtConfig.jwtSecretKey();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractDairyCenterId(String token) {
        return extractClaim(token, claims -> {
            Object id = claims.get("dairyCenterId");
            return id instanceof Long ? (Long) id : Long.valueOf(id.toString());
        });
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generate access token (short-lived).
     */
    public String generateAccessToken(UserDetails userDetails, Long dairyCenterId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("dairyCenterId", dairyCenterId);
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername(), jwtConfig.getAccessTokenExpiration());
    }

    /**
     * Generate refresh token (long-lived, minimal claims for security).
     */
    public String generateRefreshToken(UserDetails userDetails, Long dairyCenterId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("dairyCenterId", dairyCenterId);
        claims.put("sub", userDetails.getUsername());  // Minimal: just subject for refresh
        return createToken(claims, userDetails.getUsername(), jwtConfig.getRefreshTokenExpiration());
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validate refresh token (checks expiration and subject only).
     */
    public Boolean validateRefreshToken(String token, String expectedUsername) {
        final String username = extractUsername(token);
        return (username.equals(expectedUsername) && !isTokenExpired(token));
    }
}
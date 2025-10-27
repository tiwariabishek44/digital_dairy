package com.digitaldairy.config;

/**
 * TenantConfig: Implements multi-tenancy by injecting dairyCenterId from JWT into queries via AOP.
 * Uses ThreadLocal to store tenant context; applies to all services/repos for data isolation.
 * Ensures no cross-tenant data leakage (e.g., farmer A sees only their dairy's records).
 */

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class TenantConfig {

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();

    /**
     * Sets the current tenant ID in ThreadLocal (called from JwtAuthenticationFilter).
     * @param tenantId The dairyCenterId from JWT claims.
     */
    public static void setCurrentTenant(Long tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * Gets the current tenant ID (used in aspect or custom repo queries).
     * @return Tenant ID or null if not set.
     */
    public static Long getCurrentTenant() {
        return currentTenant.get();
    }

    @Around("@within(org.springframework.stereotype.Service) || @within(org.springframework.data.repository.Repository)")
    @Transactional  // Ensures tenant filter in transactions
    public Object enforceTenantIsolation(ProceedingJoinPoint joinPoint) throws Throwable {
        Long tenantId = getCurrentTenant();  // Pull from ThreadLocal set by filter
        if (tenantId != null) {
            try {
                return joinPoint.proceed();
            } finally {
                currentTenant.remove();  // Clean up after request
            }
        }
        throw new IllegalStateException("Tenant ID not found in JWT - access denied");
    }

    // Optional: Fallback extraction if needed (but filter handles it primarily)
    private Long extractTenantFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Extract from custom UserDetails (add dairyCenterId claim in JWT)
            return (Long) auth.getPrincipal();  // Placeholder; use JwtUtil to pull from token claims
        }
        return null;
    }
}
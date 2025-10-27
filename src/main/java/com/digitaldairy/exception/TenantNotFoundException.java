package com.digitaldairy.exception;

/**
 * TenantNotFoundException: Custom exception thrown when a dairyCenterId (tenant) is not found.
 * Used in services/repos to handle invalid tenant access (e.g., deleted dairy center).
 * Returns 404 in API responses via GlobalExceptionHandler.
 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TenantNotFoundException extends RuntimeException {

    public TenantNotFoundException(String message) {
        super(message);
    }

    public TenantNotFoundException(Long tenantId) {
        super("Dairy center (tenant ID: " + tenantId + ") not found.");
    }
}
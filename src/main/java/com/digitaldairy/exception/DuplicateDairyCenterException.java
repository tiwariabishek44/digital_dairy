package com.digitaldairy.exception;

/**
 * DuplicateDairyCenterException: Custom exception for duplicate dairy center name attempts.
 * Thrown in TenantService when onboarding fails due to existing name.
 * Returns 400 BAD_REQUEST via GlobalExceptionHandler.
 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateDairyCenterException extends RuntimeException {

    public DuplicateDairyCenterException(String dairyCenterName) {
        super("Dairy center with name '" + dairyCenterName + "' already exists. Please use a unique name.");
    }

    public DuplicateDairyCenterException(String message, Throwable cause) {
        super(message, cause);
    }
}
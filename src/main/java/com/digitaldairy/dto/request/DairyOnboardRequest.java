package com.digitaldairy.dto.request;

/**
 * DairyOnboardRequest: DTO for creating/onboarding a new DairyCenter (tenant).
 * Used in /api/dairy/onboard POST (admin-only, e.g., via Postman for initial setup).
 * Validation: @NotBlank on name/location; contact optional.
 * Service maps to DairyCenter entity (sets isActive=true by default).
 */

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DairyOnboardRequest {

    @NotBlank(message = "Dairy center name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    private String contact;  // Optional phone/email
}
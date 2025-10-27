package com.digitaldairy.dto.response;

/**
 * DairyOnboardResponse: DTO for /api/dairy/onboard success response.
 * Returns created DairyCenter details (id, name, location, contact) for confirmation.
 * Includes message for UX (e.g., "Chitwan Dairy Center onboarded successfully").
 * Used after service save + repo flush.
 */

import lombok.Data;

@Data
public class DairyOnboardResponse {

    private Long id;  // Auto-generated DB ID

    private String name;

    private String location;

    private String contact;

    private String message;  // e.g., "Dairy center created successfully"

    // Default constructor for JSON mapping
    public DairyOnboardResponse() {}

    // Convenience constructor
    public DairyOnboardResponse(Long id, String name, String location, String contact, String message) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.contact = contact;
        this.message = message;
    }
}
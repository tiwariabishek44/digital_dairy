package com.digitaldairy.dto.response;

/**
 * LoginResponse: DTO for /api/auth/login success.
 * Common fields for both farmer/staff: token, username (phone), dairyId (dairyCenterId), dairyName.
 * Farmer-only: dairyGivenId (null for staff).
 * Fetched via DairyCenter join in AuthService for dairyName.
 * Mobile app uses role (from token claims) to handle conditional fields.
 */

import lombok.Data;

@Data
public class LoginResponse {

    private String token;  // JWT access token

    private String username;  // Phone as username

    private Long dairyId;  // dairyCenterId

    private String dairyName;  // e.g., "Chitwan Dairy Center"

    private String dairyGivenId;  // Dairy-provided ID (null for staff)

    private String message;  // e.g., "Login successful"

    // Default constructor for JSON mapping
    public LoginResponse() {}

    // Convenience constructor
    public LoginResponse(String token, String username, Long dairyId, String dairyName,
                         String dairyGivenId, String message) {
        this.token = token;
        this.username = username;
        this.dairyId = dairyId;
        this.dairyName = dairyName;
        this.dairyGivenId = dairyGivenId;
        this.message = message;
    }
}
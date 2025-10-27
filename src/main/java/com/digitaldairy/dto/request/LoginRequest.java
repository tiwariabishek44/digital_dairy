package com.digitaldairy.dto.request;

/**
 * LoginRequest: DTO for /api/auth/login POST.
 * For farmers: phone + dairyGivenId + password (composite key).
 * For staff: phone + password (dairyGivenId ignored/null).
 * Validation: @NotBlank on required fields; phone regex for Nepal format if needed.
 */

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String dairyGivenId;  // Required for farmers, optional for staff

    @NotBlank(message = "Password is required")
    private String password;
}
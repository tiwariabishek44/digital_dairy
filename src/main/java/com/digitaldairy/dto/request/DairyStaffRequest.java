package com.digitaldairy.dto.request;

/**
 * DairyStaffRequest: DTO for creating dairy staff accounts.
 * Used in POST /api/staff/create (admin/existing staff creates new staff via Postman).
 *
 * Validation:
 * - name: Required, min 2 chars
 * - phone: Required, Nepal format (10 digits starting with 98)
 * - password: Required, min 6 chars
 * - dairyCenterId: Required, must exist in database
 *
 * No OTP - direct account creation for staff members.
 */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DairyStaffRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^98\\d{8}$", message = "Phone must be 10 digits starting with 98")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Dairy Center ID is required")
    private Long dairyCenterId;
}
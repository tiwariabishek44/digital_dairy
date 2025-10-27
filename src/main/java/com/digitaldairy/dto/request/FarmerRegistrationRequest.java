package com.digitaldairy.dto.request;

/**
 * FarmerRegistrationRequest: DTO for farmer self-registration.
 */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FarmerRegistrationRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^98\\d{8}$", message = "Phone must be 10 digits starting with 98")
    private String phone;

    @NotBlank(message = "Dairy Given ID is required")
    private String dairyGivenId;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Dairy Center ID is required")
    private Long dairyCenterId;
}
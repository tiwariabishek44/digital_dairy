package com.digitaldairy.dto.response;

/**
 * FarmerRegistrationResponse: DTO for farmer registration success.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerRegistrationResponse {

    private Long id;
    private String name;
    private String phone;
    private String dairyGivenId;
    private Long dairyCenterId;
    private String dairyCenterName;
    private LocalDateTime createdAt;
}
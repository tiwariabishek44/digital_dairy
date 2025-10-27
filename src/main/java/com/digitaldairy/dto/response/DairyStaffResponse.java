package com.digitaldairy.dto.response;

/**
 * DairyStaffResponse: DTO for dairy staff account details.
 * Used in:
 * - Account creation response
 * - Staff listing
 * - Staff profile retrieval
 *
 * Excludes password for security.
 * Includes dairy center info for context.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DairyStaffResponse {

    private Long id;

    private String name;

    private String phone;

    private Long dairyCenterId;

    private String dairyCenterName;  // For display purposes

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
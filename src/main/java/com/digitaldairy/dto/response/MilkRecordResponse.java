package com.digitaldairy.dto.response;

/**
 * MilkRecordResponse: DTO for returning milk record details.
 * Used in API responses when querying milk records.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilkRecordResponse {

    private Long id;
    private LocalDate collectionDate;
    private String nepaliDate;
    private String nepaliMonth;
    private String nepaliYear;
    private LocalTime collectionTime;
    private String memberCode;
    private String farmerName;
    private Double volumeLiters;
    private Double fatPercentage;
    private Double snf;
    private Double rate;
    private Double amount;
    private String remarks;
    private Long dairyCenterId;
    private String dairyCenterName;
    private LocalDateTime createdAt;
}
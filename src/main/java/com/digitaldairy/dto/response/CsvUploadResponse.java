package com.digitaldairy.dto.response;

/**
 * CsvUploadResponse: DTO for CSV upload operation results.
 * Returns summary of processing: success count, failure count, error details.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvUploadResponse {

    private int totalRecords;        // Total rows in CSV
    private int successfulRecords;   // Successfully saved
    private int failedRecords;       // Failed to save
    private List<ErrorDetail> errors; // List of errors with row numbers

    public CsvUploadResponse(int totalRecords, int successfulRecords, int failedRecords) {
        this.totalRecords = totalRecords;
        this.successfulRecords = successfulRecords;
        this.failedRecords = failedRecords;
        this.errors = new ArrayList<>();
    }

    public void addError(int rowNumber, String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(new ErrorDetail(rowNumber, error));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private int rowNumber;
        private String error;
    }
}
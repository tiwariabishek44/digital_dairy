package com.digitaldairy.dto.response;

/**
 * ApiResponse: Generic wrapper DTO for standardized API responses (success/error).
 * Supports any payload type <T> (e.g., LoginResponse, list of MilkRecords).
 * Used in controllers for consistent JSON: {success: true/false, data: T, message: str, timestamp: ISO}.
 * Error cases: success=false, data=null, message=error details.
 * Mobile app parses success flag to handle UI (e.g., show loading on true).
 */

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ApiResponse<T> {

    private boolean success;

    private T data;

    private String message;

    private String timestamp;  // ISO format for client parsing

    // Default constructor for JSON mapping
    public ApiResponse() {}

    // Success constructor
    public ApiResponse(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Error constructor
    public ApiResponse(String message) {
        this.success = false;
        this.data = null;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Full constructor for flexibility
    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
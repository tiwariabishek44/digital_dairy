package com.digitaldairy.dto.response;

/**
 * ApiResponse: Generic wrapper DTO for standardized API responses (success/error).
 * Supports any payload type <T> (e.g., LoginResponse, list of MilkRecords).
 * Used in controllers for consistent JSON: {success: true/false, data: T, message: str, timestamp: ISO}.
 * Error cases: success=false, data=null, message=error details.
 * Mobile app parses success flag to handle UI (e.g., show loading on true).
 *
 * UPDATED: Added static factory methods for common response scenarios:
 * - success() - Standard success with data
 * - emptyList() - Success with empty list (for queries returning no results)
 * - empty() - Success with null data (for operations with no return value)
 * - error() - Error response
 *
 * Usage Examples:
 * - ApiResponse.success(data, "Retrieved successfully")
 * - ApiResponse.emptyList("No farmers found")
 * - ApiResponse.empty("Deleted successfully")
 * - ApiResponse.error("Validation failed")
 */

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Data
public class ApiResponse<T> {

    private boolean success;

    private T data;

    private String message;

    private String timestamp;  // ISO format for client parsing

    // ========== CONSTRUCTORS ==========

    /**
     * Default constructor for JSON mapping.
     */
    public ApiResponse() {}

    /**
     * Success constructor - most common case.
     * Used when operation succeeds and returns data.
     */
    public ApiResponse(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Error constructor - used by GlobalExceptionHandler.
     * Used when operation fails.
     */
    public ApiResponse(String message) {
        this.success = false;
        this.data = null;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Full constructor for flexibility.
     * Allows custom success flag (rare cases).
     */
    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // ========== STATIC FACTORY METHODS (RECOMMENDED) ==========

    /**
     * Creates a success response with data.
     *
     * Use for: Normal successful operations that return data
     * Returns: 200 OK with data
     *
     * Example:
     * return ResponseEntity.ok(ApiResponse.success(farmer, "Farmer retrieved"));
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    /**
     * Creates a success response with empty list.
     *
     * Use for: List/search endpoints that return no results (still successful query)
     * Returns: 200 OK with empty array []
     * HTTP Status: 200 OK (NOT 404 - the query succeeded, just no data)
     *
     * Example:
     * if (farmers.isEmpty()) {
     *     return ResponseEntity.ok(ApiResponse.emptyList("No farmers registered yet"));
     * }
     */
    public static <T> ApiResponse<List<T>> emptyList(String message) {
        return new ApiResponse<>(true, Collections.emptyList(), message);
    }

    /**
     * Creates a success response with null data.
     *
     * Use for: Operations that succeed but don't return data
     * Examples: Delete operations, update confirmations, void operations
     * Returns: 200 OK with data=null
     *
     * Example:
     * return ResponseEntity.ok(ApiResponse.empty("Farmer deleted successfully"));
     */
    public static <T> ApiResponse<T> empty(String message) {
        return new ApiResponse<>(true, null, message);
    }

    /**
     * Creates an error response.
     *
     * Use for: Business rule violations, validation errors
     * Returns: Error response with success=false, data=null
     * Note: GlobalExceptionHandler typically creates these, but can be used manually
     *
     * Example:
     * return ResponseEntity.badRequest().body(ApiResponse.error("Invalid phone number"));
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    /**
     * Creates an error response with custom success flag.
     *
     * Use for: Rare cases where you need custom error handling
     * Most of the time, use error(message) instead
     */
    public static <T> ApiResponse<T> error(String message, boolean success) {
        return new ApiResponse<>(success, null, message);
    }

    // ========== CONVENIENCE METHODS ==========

    /**
     * Check if response contains data.
     * Useful for client-side validation.
     */
    public boolean hasData() {
        return this.data != null;
    }

    /**
     * Check if response is successful with data.
     */
    public boolean isSuccessWithData() {
        return this.success && this.data != null;
    }

    /**
     * Check if response is an error.
     */
    public boolean isError() {
        return !this.success;
    }
}
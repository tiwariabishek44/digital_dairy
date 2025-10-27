package com.digitaldairy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTenantNotFound(TenantNotFoundException ex) {
        log.warn("Tenant not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(DuplicateDairyCenterException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateDairyCenter(DuplicateDairyCenterException ex) {
        log.warn("Duplicate dairy center attempt: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(InvalidCsvException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCsv(InvalidCsvException ex) {
        log.error("Invalid CSV processing: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(FcmSendException.class)
    public ResponseEntity<ErrorResponse> handleFcmSend(FcmSendException ex) {
        // Log full stacktrace internally; client sees generic msg for security
        log.error("FCM notification failed", ex);
        return buildErrorResponse(
                "Notification service temporarily unavailable. Please try again.",
                HttpStatus.SERVICE_UNAVAILABLE.value()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // Build cleaner validation error message
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", errorMessage);
        return buildErrorResponse("Validation failed - " + errorMessage, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedOperation(UnsupportedOperationException ex) {
        log.warn("Unsupported operation: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED.value());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGeneric(RuntimeException ex) {
        // Log full exception for debugging
        log.error("Unexpected runtime exception", ex);
        return buildErrorResponse(
                "Internal server error. Please contact support.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        // Catch-all for any unexpected checked exceptions
        log.error("Unexpected exception", ex);
        return buildErrorResponse(
                "An unexpected error occurred. Please contact support.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, int statusCode) {
        ErrorResponse response = new ErrorResponse(
                message,
                statusCode,
                LocalDateTime.now().format(DATE_FORMAT)
        );
        return new ResponseEntity<>(response, HttpStatus.valueOf(statusCode));
    }

    // Inner DTO for error JSON
    public static class ErrorResponse {
        private String error;
        private int code;
        private String timestamp;

        public ErrorResponse(String error, int code, String timestamp) {
            this.error = error;
            this.code = code;
            this.timestamp = timestamp;
        }

        // Getters/setters
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}
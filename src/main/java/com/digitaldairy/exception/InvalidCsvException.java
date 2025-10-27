package com.digitaldairy.exception;

/**
 * InvalidCsvException: Custom exception for CSV import failures (e.g., milk records parsing errors).
 * Thrown in MilkRecordingService during bulk uploads (malformed data, wrong columns).
 * Returns 400 with details in API response for dairy staff debugging.
 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCsvException extends RuntimeException {

    public InvalidCsvException(String message) {
        super(message);
    }

    public InvalidCsvException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.digitaldairy.exception;

/**
 * CsvProcessingException: Custom exception for CSV processing errors.
 * Thrown during milk record CSV upload when parsing or validation fails.
 * Returns 400 BAD_REQUEST via GlobalExceptionHandler.
 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CsvProcessingException extends RuntimeException {

    public CsvProcessingException(String message) {
        super(message);
    }

    public CsvProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.digitaldairy.exception;

/**
 * FcmSendException: Custom exception for Firebase FCM push failures (e.g., broadcast/OTP sends).
 * Thrown in NotificationService if token invalid or quota hit.
 * Returns 503 for retryable errors; logs details without exposing to client.
 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class FcmSendException extends RuntimeException {

    public FcmSendException(String message) {
        super(message);
    }

    public FcmSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
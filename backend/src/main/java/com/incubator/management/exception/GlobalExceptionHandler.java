package com.incubator.management.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * ------------------------
 * Intercepts exceptions thrown anywhere in the application
 * and converts them into structured JSON error responses.
 *
 * This prevents raw stack traces from leaking to the client
 * and ensures all error responses have a consistent format:
 *   { "error": "message here" }
 *
 * @ControllerAdvice makes this apply to all @RestController classes globally.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle intentional runtime exceptions (e.g. "User not found", "Email already exists").
     * Returns HTTP 400 Bad Request with the exception's message.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(400).body(error);
    }

    /**
     * Catch-all handler for unexpected errors (e.g. database connection failure).
     * Returns HTTP 500 Internal Server Error with a generic message.
     * The real error is hidden from the client for security.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An internal server error occurred");
        return ResponseEntity.status(500).body(error);
    }
}

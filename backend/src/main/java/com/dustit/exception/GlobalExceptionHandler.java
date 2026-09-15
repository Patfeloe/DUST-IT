package com.dustit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Turns exceptions into clean JSON error responses instead of a raw
 * stack trace leaking back to the client (Section 22: secure API
 * endpoints).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAssessmentOperationException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAssessmentOperation(InvalidAssessmentOperationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}
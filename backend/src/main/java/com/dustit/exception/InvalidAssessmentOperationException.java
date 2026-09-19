package com.dustit.exception;

/**
 * Thrown for invalid assessment operations - e.g. submitting an answer
 * for a question that doesn't belong to the attempt's assessment, or
 * submitting an answer to an already-completed attempt.
 */
public class InvalidAssessmentOperationException extends RuntimeException {

    public InvalidAssessmentOperationException(String message) {
        super(message);
    }
}

package com.pharmatrace.api.common;

import java.time.Instant;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pharmatrace.api.batch.BatchNotFoundException;
import com.pharmatrace.api.batch.GraphUnavailableException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BatchNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(BatchNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "BATCH_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(GraphUnavailableException.class)
    ResponseEntity<ApiError> handleDatabaseUnavailable(GraphUnavailableException exception) {
        return response(
                HttpStatus.SERVICE_UNAVAILABLE,
                "GRAPH_UNAVAILABLE",
                "The supply-chain graph is temporarily unavailable. Please try again shortly.");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiError> handleValidation(ConstraintViolationException exception) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), code, message));
    }
}

// Created by Arjunsingh Rajpurohit.
package com.numa.generic;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.dev.mode}")
    private boolean devMode;

    /* ---------------------------------------------
     * MAIN EXCEPTION HANDLER
     * --------------------------------------------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Exception occurred: {}", e.getMessage(), e);

        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause instanceof SQLException sqlException) {
                return handleSqlException(sqlException, request);
            }
            cause = cause.getCause();
        }

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                devMode ? e.getMessage() : "Internal server error",
                e,
                request
        );
    }

    /* ---------------------------------------------
     * SQL EXCEPTION HANDLING
     * --------------------------------------------- */
    private ResponseEntity<ErrorResponse> handleSqlException(SQLException sql, HttpServletRequest request) {
        log.error("SQL Error (Code {}): {}", sql.getErrorCode(), sql.getMessage());

        String message;
        HttpStatus status = HttpStatus.CONFLICT;

        message = switch (sql.getErrorCode()) {
            case 547 -> // Foreign key constraint
                    sql.getMessage().toLowerCase().contains("delete")
                            ? "Unable to delete due to dependent records"
                            : "Referenced record does not exist"; // Unique constraint
            case 2627, 2601 -> {
                message = devMode ? sql.getMessage() : "A record with the same unique value already exists";
                yield "Duplicate entry: " + message;
            }
            default -> devMode ? sql.getMessage() : "Database error occurred";
        };

        return buildError(status, message, sql, request);
    }

    /* ---------------------------------------------
     * SPECIFIC EXCEPTION TYPES
     * --------------------------------------------- */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e,
                                                                       HttpServletRequest request) {
        log.warn("ResponseStatusException: {}", e.getReason());
        return buildError(e.getStatusCode(), e.getReason(), e, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e,
                                                                        HttpServletRequest request) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        return buildError(
                HttpStatus.BAD_REQUEST,
                devMode ? e.getMessage() : "Invalid input provided",
                e,
                request
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e,
                                                                       HttpServletRequest request) {
        log.warn("EntityNotFoundException: {}", e.getMessage());
        return buildError(
                HttpStatus.NOT_FOUND,
                devMode ? e.getMessage() : "Requested data could not be found",
                e,
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e,
                                                                   HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(err -> {
            String field = ((FieldError) err).getField();
            String msg = err.getDefaultMessage();
            errors.put(field, msg);
        });

        String message = errors.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", message);

        return buildError(HttpStatus.BAD_REQUEST, message, e, request);
    }

    /* ---------------------------------------------
     * COMMON ERROR RESPONSE BUILDER
     * --------------------------------------------- */
    private ResponseEntity<ErrorResponse> buildError(HttpStatusCode status,
                                                     String message,
                                                     Exception ex,
                                                     HttpServletRequest request) {

        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
                .status(status.value())
                .error(status instanceof HttpStatus ? ((HttpStatus) status).getReasonPhrase() : "")
                .message(message)
                .path(request.getRequestURI());

        if (devMode) {
            builder.exception(ex.getClass().getName());
            builder.detailedMessage(ex.getMessage());
        }

        return ResponseEntity.status(status.value()).body(builder.build());
    }

    /* ---------------------------------------------
     * ERROR RESPONSE MODEL
     * --------------------------------------------- */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private String path;

        // Only in DEV mode
        private String exception;
        private String detailedMessage;
    }
}
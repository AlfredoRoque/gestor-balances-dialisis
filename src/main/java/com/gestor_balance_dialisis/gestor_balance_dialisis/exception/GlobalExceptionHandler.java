package com.gestor_balance_dialisis.gestor_balance_dialisis.exception;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles validation errors, custom exceptions, email sending errors, and SQL exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors and returns a map of field names and error messages.
     *
     * @param ex The exception containing validation errors.
     * @return A response entity containing a map of field names and error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles custom global exceptions and returns a response entity containing the error code and message.
     *
     * @param ex The custom global exception.
     * @return A response entity containing the error code and message.
     */
    @ExceptionHandler(BalanceGlobalException.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(BalanceGlobalException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("code", ex.getCode());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(ex.getCode()).body(response);
    }

    /**
     * Handles email sending errors and returns a response entity containing the error code and message.
     *
     * @param ex The exception containing email sending errors.
     * @return A response entity containing the error code and message.
     */
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<Map<String, Object>> handleEmailException(MessagingException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("code", HttpStatus.CONFLICT.value());
        response.put("message", "Error sending email.");

        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(response);
    }

    /**
     * Handles SQL exceptions and returns a response entity containing the error code and message.
     *
     * @param ex The exception containing SQL errors.
     * @return A response entity containing the error code and message.
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, Object>> handleEmailNotFound(SQLException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("code", HttpStatus.CONFLICT.value());
        response.put("message", "Error to register, update or get user information, try again later.");

        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(response);
    }
}

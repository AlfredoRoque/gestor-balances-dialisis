package com.gestor_balance_dialisis.gestor_balance_dialisis.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the GlobalExceptionHandler class, which handles various exceptions thrown by the application and maps them to appropriate HTTP responses. This test class verifies that the exception handler correctly processes validation errors, custom global exceptions, SQL exceptions, and generic exceptions, ensuring that the correct HTTP status codes and response bodies are returned for each case.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks private GlobalExceptionHandler handler;

    // ─── handleValidationErrors ───────────────────────────────────────────────

    /**
     * Test that handleValidationErrors returns a BAD_REQUEST response with field error details.
     * Verifies that when a single validation error occurs, the handler maps the field name
     * to its error message and returns an HTTP 400 response.
     */
    @Test
    void handleValidationErrors_returnsFieldErrorMap() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "username", "Username is required");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("username", "Username is required");
    }

    /**
     * Test that handleValidationErrors returns all field errors for multiple validation failures.
     * Verifies that when multiple fields fail validation, the handler includes all field names
     * and their respective error messages in the response body.
     */
    @Test
    void handleValidationErrors_multipleErrors_returnsAllFields() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> errors = List.of(
                new FieldError("obj", "email", "Email required"),
                new FieldError("obj", "password", "Password required")
        );

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(ex);

        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsKey("email");
        assertThat(response.getBody()).containsKey("password");
    }

    // ─── handleGlobalException ────────────────────────────────────────────────

    /**
     * Test that handleGlobalException returns the correct HTTP status code and message.
     * Verifies that a BalanceGlobalException with a 404 code produces a response
     * containing the message, code, and a timestamp field.
     */
    @Test
    void handleGlobalException_returnsCorrectCodeAndMessage() {
        BalanceGlobalException ex = new BalanceGlobalException("Paciente no encontrado", 404);

        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).containsEntry("message", "Paciente no encontrado");
        assertThat(response.getBody()).containsEntry("code", 404);
        assertThat(response.getBody()).containsKey("timestamp");
    }

    /**
     * Test that handleGlobalException handles CONFLICT status correctly.
     * Verifies that a BalanceGlobalException with HTTP 409 code produces a response
     * with the matching status code.
     */
    @Test
    void handleGlobalException_conflictStatus_returnsConflict() {
        BalanceGlobalException ex = new BalanceGlobalException("Ya existe", HttpStatus.CONFLICT.value());

        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody().get("code")).isEqualTo(409);
    }

    // ─── handleEmailNotFound (SQLException) ──────────────────────────────────

    /**
     * Test that handleEmailNotFound returns a CONFLICT response for SQL exceptions.
     * Verifies that when a SQLException is thrown, the handler returns an HTTP 409 response
     * with a message and code in the response body.
     */
    @Test
    void handleEmailNotFound_sqlException_returnsConflict() {
        SQLException ex = new SQLException("DB error");

        ResponseEntity<Map<String, Object>> response = handler.handleEmailNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("code")).isEqualTo(409);
    }

    // ─── handleGenericException ───────────────────────────────────────────────

    /**
     * Test that handleGenericException returns an INTERNAL_SERVER_ERROR response.
     * Verifies that any unhandled exception results in an HTTP 500 response
     * with a generic error message.
     */
    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("message", "An unexpected error occurred.");
        assertThat(response.getBody().get("code")).isEqualTo(500);
    }
}


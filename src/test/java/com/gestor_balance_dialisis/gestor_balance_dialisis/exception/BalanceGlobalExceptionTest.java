package com.gestor_balance_dialisis.gestor_balance_dialisis.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the BalanceGlobalException class, which is a custom exception used to represent application-specific errors with associated HTTP status codes. These tests verify that the exception correctly stores and retrieves the message and code, and that it extends RuntimeException as expected.
 */
class BalanceGlobalExceptionTest {

    /**
     * Test that the constructor correctly sets the message and HTTP status code.
     * Verifies that the exception's getMessage() returns the provided message
     * and getCode() returns the provided status code.
     */
    @Test
    void constructor_setsMessageAndCode() {
        BalanceGlobalException ex = new BalanceGlobalException("Error message", 404);

        assertThat(ex.getMessage()).isEqualTo("Error message");
        assertThat(ex.getCode()).isEqualTo(404);
    }

    /**
     * Test that BalanceGlobalException extends RuntimeException.
     * Verifies that the exception is an unchecked exception, allowing it to propagate
     * without explicit throws declarations.
     */
    @Test
    void isRuntimeException() {
        BalanceGlobalException ex = new BalanceGlobalException("Test", 500);

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    /**
     * Test that different HTTP status codes are stored correctly in separate instances.
     * Verifies that each exception instance independently holds its assigned code value.
     */
    @Test
    void differentCodes_areStoredCorrectly() {
        BalanceGlobalException conflict = new BalanceGlobalException("Conflict", 409);
        BalanceGlobalException notFound = new BalanceGlobalException("Not Found", 404);

        assertThat(conflict.getCode()).isEqualTo(409);
        assertThat(notFound.getCode()).isEqualTo(404);
    }
}

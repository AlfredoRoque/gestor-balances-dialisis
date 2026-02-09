package com.gestor_balance_dialisis.gestor_balance_dialisis.exception;

import lombok.Getter;

/**
 * Exception class for handling global exceptions in the application. It extends RuntimeException and includes an additional field for an error code.
 */
@Getter
public class BalanceGlobalException extends RuntimeException {

    private final int code;

    /**
     * Constructs a new BalanceGlobalException with the specified message and error code.
     *
     * @param message the detail message for the exception
     * @param code    the error code associated with the exception
     */
    public BalanceGlobalException(String message, int code) {
        super(message);
        this.code = code;
    }

}

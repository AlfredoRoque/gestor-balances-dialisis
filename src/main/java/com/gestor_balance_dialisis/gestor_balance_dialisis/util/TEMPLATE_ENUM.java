package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import lombok.Getter;

/**
 * Enum representing different email templates used in the application.
 * Each enum constant corresponds to a specific email template, identified by a unique string value.
 * The enum provides a convenient way to manage and reference email templates throughout the application.
 */
@Getter
public enum TEMPLATE_ENUM {

    TEMPLATE_RECOVER_PASSWORD("RECOVER_PASSWORD"),
    TEMPLATE_BALANCE_REPORT("BALANCE_REPORT");

    private final String value;

    /**
     * Constructor for the TEMPLATE_ENUM enum.
     *
     * @param value The string value associated with the enum constant, representing the name of the email template.
     */
    TEMPLATE_ENUM(String value) {
        this.value = value;
    }

}

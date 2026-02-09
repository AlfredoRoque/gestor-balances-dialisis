package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

/**
 * Utility class for common helper methods.
 */
@UtilityClass
public class Utility {

    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random temporary password of the specified length.
     *
     * @param length The desired length of the temporary password.
     * @return A randomly generated temporary password consisting of uppercase letters, lowercase letters, and digits.
     */
    public static String generateTemporaryPassword(int length) {
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARS.length());
            password.append(CHARS.charAt(index));
        }

        return password.toString();
    }
}

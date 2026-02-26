package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.time.*;

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

    /**
     * Returns the start of the day (00:00:00) for the given Instant in UTC.
     *
     * @param instant The Instant for which to calculate the start of the day.
     * @return An Instant representing the start of the day in UTC.
     */
    public static Instant startDay(Instant instant) {
        ZoneId zone = SecurityUtils.getUserZone();
        return instant
                .atZone(zone)
                .toLocalDate()
                .atStartOfDay(zone)
                .toInstant();
    }

    /**
     * Returns the end of the day (23:59:59.999999999) for the given Instant in UTC.
     *
     * @param instant The Instant for which to calculate the end of the day.
     * @return An Instant representing the end of the day in UTC.
     */
    public static Instant endDay(Instant instant) {
        ZoneId zone = SecurityUtils.getUserZone();
        return instant
                .atZone(zone)
                .toLocalDate()
                .atTime(LocalTime.MAX)
                .atZone(zone)
                .toInstant();
    }
}

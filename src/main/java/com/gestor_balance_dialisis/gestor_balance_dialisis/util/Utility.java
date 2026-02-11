package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

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
     * Converts a LocalDateTime object to a Date object.
     *
     * @param date The LocalDateTime object to be converted.
     * @return A Date object representing the same point in time as the provided LocalDateTime.
     */
    public static Date startDay(Date date) {
        ZoneId zone = ZoneId.systemDefault();

        LocalDate localDate = date.toInstant()
                .atZone(zone)
                .toLocalDate();

        return Date.from(localDate.atStartOfDay(zone).toInstant());
    }

    /**
     * Converts a LocalDateTime object to a Date object representing the end of the day.
     *
     * @param date The LocalDateTime object to be converted.
     * @return A Date object representing the end of the day for the provided LocalDateTime.
     */
    public static Date endDay(Date date) {
        ZoneId zone = ZoneId.systemDefault();

        LocalDate localDate = date.toInstant()
                .atZone(zone)
                .toLocalDate();

        return Date.from(localDate.atTime(LocalTime.MAX).atZone(zone).toInstant());
    }

    /**
     * Converts a LocalDateTime object to a Date object.
     *
     * @param date The LocalDateTime object to be converted.
     * @return A Date object representing the same point in time as the provided LocalDateTime.
     */
    public Date convertLocalDateTimeToDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }
}

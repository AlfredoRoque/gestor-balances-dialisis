package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Utility class for security-related operations, such as retrieving the user's time zone from the security context.
 * This class provides methods to access security information and perform common security-related tasks.
 */
@UtilityClass
public class SecurityUtils {

    /**
     * Retrieves the user's time zone from the security context. If the time zone is not available, it defaults to UTC.
     *
     * @return the user's time zone as a ZoneId
     */
    public static ZoneId getUserZone() {
        Object details = SecurityContextHolder.getContext()
                .getAuthentication()
                .getDetails();

        if (details instanceof String zone) {
            return ZoneId.of(zone);
        }

        return ZoneOffset.UTC; // fallback seguro
    }
}

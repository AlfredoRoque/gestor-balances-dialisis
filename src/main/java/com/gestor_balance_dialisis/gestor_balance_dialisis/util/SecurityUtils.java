package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserSessionModel;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.RsaKeyService;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for security-related operations, such as retrieving the user's time zone from the security context.
 * This class provides methods to access security information and perform common security-related tasks.
 */
@Slf4j
@UtilityClass
public class SecurityUtils {

    /**
     * Generates a map of claims for the given user, including the user's time zone, token version, and user ID.
     *
     * @param user     The user for whom the claims are being generated.
     * @param timeZone The time zone to be included in the claims.
     * @return A map containing the user's claims.
     */
    public static Map<String, Object> getUserClaims(User user, String timeZone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("zone", timeZone);
        claims.put("version", user.getTokenVersion().intValue());
        claims.put("userId", user.getId());

        return claims;
    }

    /**
     * Retrieves the user's time zone from the security context. If the time zone is not available, it defaults to UTC.
     *
     * @return the user's time zone as a ZoneId
     */
    public static ZoneId getUserZone() {
        UserSessionModel userSession = (UserSessionModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (Objects.nonNull(userSession.getZone())) {
            return ZoneId.of(userSession.getZone());
        }

        return ZoneOffset.UTC;
    }

    /**
     * Retrieves the user's ID from the security context. If the user ID is not available, it defaults to 0L.
     *
     * @return the user's ID as a Long
     */
    public static Long getUserId() {
        UserSessionModel userSession = (UserSessionModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (Objects.nonNull(userSession.getUserId())) {
            return userSession.getUserId().longValue();
        }
        return 0L;
    }

    /**
     * Decrypts the given encrypted password using the provided RsaKeyService. If decryption fails, it throws a BalanceGlobalException.
     *
     * @param encryptedPassword The encrypted password to be decrypted.
     * @param rsaKeyService    The RsaKeyService used for decryption.
     * @return The decrypted raw password as a String.
     * @throws BalanceGlobalException if password decryption fails.
     */
    public static String decryptPassword(String encryptedPassword, RsaKeyService rsaKeyService) {
        String rawPassword;
        try {
            rawPassword = rsaKeyService.decrypt(encryptedPassword);
        } catch (Exception e) {
            log.warn("Password decryption failed {}", e.getMessage());
            throw new BalanceGlobalException("Invalid credentials", HttpStatus.CONFLICT.value());
        }
        return rawPassword;
    }
}

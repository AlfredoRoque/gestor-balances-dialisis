package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserSessionModel;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.RsaKeyService;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
     * Constructs a map of user claims to be included in the JWT token. The claims include the user's time zone, token version, and user ID.
     *
     * @param tokenVersion the version of the token, used for invalidating old tokens when necessary.
     * @param id           the ID of the user, which can be used to identify the user in the system.
     * @param timeZone     the time zone of the user, which can be used for time-related operations in the application.
     * @return a map containing the user claims to be included in the JWT token.
     */
    public static Map<String, Object> getUserClaims(Integer tokenVersion, Long id, UserRol rol, String email, String timeZone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("zone", timeZone);
        claims.put("version", tokenVersion);
        claims.put("role", rol.toString());
        claims.put("email", email);
        claims.put("userId", id);

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
     * Retrieves the user's email from the security context. If the email is not available, it defaults to an empty string.
     *
     * @return the user's email as a String
     */
    public static String getUserEmail() {
        UserSessionModel userSession = (UserSessionModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (Objects.nonNull(userSession.getEmail())) {
            return userSession.getEmail();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Retrieves the user's role from the security context. If the role is not available, it defaults to null.
     *
     * @return the user's role as a UserRol enum
     */
    public UserRol getUserRol() {
        UserSessionModel userSession = (UserSessionModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (Objects.nonNull(userSession.getRole())) {
            return userSession.getRole();
        }
        return null;
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
            throw new BalanceGlobalException(Constants.INVALID_CREDENTIALS, HttpStatus.CONFLICT.value());
        }
        return rawPassword;
    }
}

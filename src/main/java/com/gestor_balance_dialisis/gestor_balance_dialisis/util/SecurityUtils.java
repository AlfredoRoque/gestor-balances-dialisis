package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserSessionModel;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.RsaKeyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
     * @param user             the user entity containing details such as role, email, and ID, which are included in the token claims.
     * @param timeZone         the time zone of the user, which can be used for time-related operations in the application.
     * @return a map containing the user claims to be included in the JWT token.
     */
    public static Map<String, Object> getUserClaims(User user, String timeZone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("zone", timeZone);
        claims.put("version", user.getTokenVersion().intValue());
        claims.put("role", user.getRole().toString());
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());
        claims.put("userAdminId", user.getId());

        return claims;
    }

    public static Map<String, Object> getPatientClaims(Patient patient, String timeZone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("zone", timeZone);
        claims.put("version", patient.getTokenVersion().intValue());
        claims.put("role", patient.getRole().toString());
        claims.put("email", patient.getEmail());
        claims.put("userId", patient.getId());
        claims.put("userAdminId", patient.getUser().getId());

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
     * Retrieves the user's session information from the security context. If the session information is not available, it defaults to null.
     *
     * @return the user's session information as a UserSessionModel object
     */
    public static UserSessionModel getUserSession() {
        UserSessionModel userSession = (UserSessionModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (Objects.nonNull(userSession)) {
            return userSession;
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

    /**
     * Retrieves the JWT token from the "Authorization" header of the current HTTP request. If the request attributes are not available, it returns null.
     *
     * @return the JWT token as a String, or null if the request attributes are not available.
     */
    public String getToken() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();

        return request.getHeader("Authorization");
    }
}

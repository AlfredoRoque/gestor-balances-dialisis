package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserSessionModel;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.RsaKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the SecurityUtils class, covering methods that retrieve user information from the security context and handle password decryption.
 * Each test verifies the expected behavior of the utility methods under various conditions, including valid inputs and edge cases such as null values and invalid ciphertext.
 * The tests ensure that the utility methods correctly interact with the security context and handle exceptions as expected.
 */
class SecurityUtilsTest {

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Helper method to set up the security context with a given UserSessionModel for testing purposes.
     * @param session The UserSessionModel to be set as the authentication principal in the security context, allowing tests to simulate authenticated user scenarios with specific session data.
     */
    private void setupSecurityContext(UserSessionModel session) {
        var auth = new UsernamePasswordAuthenticationToken(session, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ─── getUserZone ──────────────────────────────────────────────────────────

    /**
     * Test that getUserZone returns the correct zone when a valid time zone is set.
     * Verifies that the ZoneId matches the zone stored in the user's session model.
     */
    @Test
    void getUserZone_withValidZone_returnsCorrectZone() {
        UserSessionModel session = new UserSessionModel();
        session.setZone("America/Mexico_City");
        setupSecurityContext(session);

        ZoneId zone = SecurityUtils.getUserZone();

        assertThat(zone).isEqualTo(ZoneId.of("America/Mexico_City"));
    }

    /**
     * Test that getUserZone defaults to UTC when the zone is null.
     * Verifies the fallback behavior when no time zone is set in the user's session.
     */
    @Test
    void getUserZone_withNullZone_returnsUtc() {
        UserSessionModel session = new UserSessionModel();
        session.setZone(null);
        setupSecurityContext(session);

        ZoneId zone = SecurityUtils.getUserZone();

        assertThat(zone).isEqualTo(ZoneOffset.UTC);
    }

    // ─── getUserId ────────────────────────────────────────────────────────────

    /**
     * Test that getUserId returns the correct user ID when it is set in the session.
     * Verifies that the Long value matches the integer stored in the session model.
     */
    @Test
    void getUserId_withValidId_returnsCorrectId() {
        UserSessionModel session = new UserSessionModel();
        session.setUserId(42);
        setupSecurityContext(session);

        Long userId = SecurityUtils.getUserId();

        assertThat(userId).isEqualTo(42L);
    }

    /**
     * Test that getUserId returns 0L when the user ID is null.
     * Verifies the default fallback when the session contains no user ID.
     */
    @Test
    void getUserId_withNullId_returnsZero() {
        UserSessionModel session = new UserSessionModel();
        session.setUserId(null);
        setupSecurityContext(session);

        Long userId = SecurityUtils.getUserId();

        assertThat(userId).isEqualTo(0L);
    }

    // ─── getUserEmail ─────────────────────────────────────────────────────────

    /**
     * Test that getUserEmail returns the correct email when it is set in the session.
     * Verifies that the returned string matches the email stored in the user's session model.
     */
    @Test
    void getUserEmail_withValidEmail_returnsEmail() {
        UserSessionModel session = new UserSessionModel();
        session.setEmail("user@mail.com");
        setupSecurityContext(session);

        String email = SecurityUtils.getUserEmail();

        assertThat(email).isEqualTo("user@mail.com");
    }

    /**
     * Test that getUserEmail returns an empty string when the email is null.
     * Verifies the fallback behavior using {@link org.apache.commons.lang3.StringUtils#EMPTY}.
     */
    @Test
    void getUserEmail_withNullEmail_returnsEmpty() {
        UserSessionModel session = new UserSessionModel();
        session.setEmail(null);
        setupSecurityContext(session);

        String email = SecurityUtils.getUserEmail();

        assertThat(email).isEmpty();
    }

    // ─── getUserRol ───────────────────────────────────────────────────────────

    /**
     * Test that getUserRol returns ADMIN when the role is set to ADMIN in the session.
     * Verifies that the {@link UserRol} enum value is correctly retrieved from the security context.
     */
    @Test
    void getUserRol_withAdminRole_returnsAdmin() {
        UserSessionModel session = new UserSessionModel();
        session.setRole(UserRol.ADMIN);
        setupSecurityContext(session);

        UserRol role = SecurityUtils.getUserRol();

        assertThat(role).isEqualTo(UserRol.ADMIN);
    }

    /**
     * Test that getUserRol returns null when no role is set in the session.
     * Verifies the fallback behavior when the session model contains a null role.
     */
    @Test
    void getUserRol_withNullRole_returnsNull() {
        UserSessionModel session = new UserSessionModel();
        session.setRole(null);
        setupSecurityContext(session);

        UserRol role = SecurityUtils.getUserRol();

        assertThat(role).isNull();
    }

    // ─── getUserSession ───────────────────────────────────────────────────────

    /**
     * Test that getUserSession returns the complete session model from the security context.
     * Verifies that the returned {@link UserSessionModel} contains the expected username.
     */
    @Test
    void getUserSession_returnsSessionModel() {
        UserSessionModel session = new UserSessionModel();
        session.setUsername("admin");
        setupSecurityContext(session);

        UserSessionModel result = SecurityUtils.getUserSession();

        assertThat(result.getUsername()).isEqualTo("admin");
    }

    // ─── getUserClaims ────────────────────────────────────────────────────────

    /**
     * Test that getUserClaims builds a claims map containing all expected keys and values.
     * Verifies that zone, version, role, email, userId, and userAdminId are present and correct
     * for an admin user.
     */
    @Test
    void getUserClaims_containsExpectedKeys() {
        User user = new User();
        user.setId(1L);
        user.setEmail("admin@mail.com");
        user.setTokenVersion(3L);
        user.setRole(UserRol.ADMIN);

        Map<String, Object> claims = SecurityUtils.getUserClaims(user, "UTC");

        assertThat(claims).containsKey("zone");
        assertThat(claims).containsKey("version");
        assertThat(claims).containsKey("role");
        assertThat(claims).containsKey("email");
        assertThat(claims).containsKey("userId");
        assertThat(claims).containsKey("userAdminId");
        assertThat(claims.get("zone")).isEqualTo("UTC");
        assertThat(claims.get("email")).isEqualTo("admin@mail.com");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
    }

    // ─── getPatientClaims ─────────────────────────────────────────────────────

    /**
     * Test that getPatientClaims builds a claims map containing all expected keys and values.
     * Verifies that the patient's userId, userAdminId (from the associated admin user),
     * zone, and role are present and correct.
     */
    @Test
    void getPatientClaims_containsExpectedKeys() {
        User adminUser = new User();
        adminUser.setId(5L);

        Patient patient = new Patient();
        patient.setId(10L);
        patient.setEmail("patient@mail.com");
        patient.setTokenVersion(2L);
        patient.setRole(UserRol.PATIENT);
        patient.setUser(adminUser);

        Map<String, Object> claims = SecurityUtils.getPatientClaims(patient, "America/New_York");

        assertThat(claims).containsKey("zone");
        assertThat(claims).containsKey("userId");
        assertThat(claims).containsKey("userAdminId");
        assertThat(claims.get("userAdminId")).isEqualTo(5L);
        assertThat(claims.get("userId")).isEqualTo(10L);
        assertThat(claims.get("role")).isEqualTo("PATIENT");
    }

    // ─── decryptPassword ──────────────────────────────────────────────────────

    /**
     * Test that decryptPassword successfully decrypts a valid RSA-encrypted password.
     * Verifies the full encryption/decryption round-trip using the RSA key pair,
     * ensuring that the original plaintext password is recovered.
     */
    @Test
    void decryptPassword_success() throws Exception {
        RsaKeyService rsaKeyService = new RsaKeyService();
        rsaKeyService.init();

        // Encrypt with the public key
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, rsaKeyService.getPublicKey());
        byte[] encrypted = cipher.doFinal("myPassword".getBytes());
        String encBase64 = java.util.Base64.getEncoder().encodeToString(encrypted);

        String result = SecurityUtils.decryptPassword(encBase64, rsaKeyService);

        assertThat(result).isEqualTo("myPassword");
    }

    /**
     * Test that decryptPassword throws a {@link BalanceGlobalException} for invalid ciphertext.
     * Verifies that an INVALID_CREDENTIALS exception is thrown when the input
     * is not a valid Base64-encoded RSA ciphertext.
     */
    @Test
    void decryptPassword_invalidCiphertext_throwsBalanceGlobalException() throws Exception {
        RsaKeyService rsaKeyService = new RsaKeyService();
        rsaKeyService.init();

        assertThatThrownBy(() -> SecurityUtils.decryptPassword("not-valid!!!", rsaKeyService))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.INVALID_CREDENTIALS);
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.security;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the JwtUtil class, covering token generation and claim extraction for both admin users and patients.
 * Tests include verifying the presence and correctness of claims such as role, user ID, and time zone, as well as handling of valid and invalid tokens during username extraction.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    /**
     * Note: The SECRET key used in these tests is a hardcoded string for testing purposes only. In a production environment, the secret should be securely stored and managed, such as in environment variables or a secrets manager, and should not be hardcoded in the source code.
     */
    // 32-byte secret for HS256 (256 bits)
    private static final String SECRET = "my-very-secret-key-for-unit-tests-32b!";
    private static final long EXPIRATION = 3_600_000L; // 1 hour

    /**
     * Set up the JwtUtil instance before each test, injecting the SECRET and EXPIRATION values using reflection since they are private fields. This allows the tests to run with a consistent configuration for token generation and validation.
     */
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "EXPIRATION", EXPIRATION);
    }

    /**
     * Helper method to build a sample User object representing an admin user. This user has a predefined ID, username, email, token version, and role. This method is used in multiple tests to provide a consistent user object for token generation.
     * @return A User object with admin role and predefined attributes for testing purposes.
     */
    private User buildUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("admin");
        u.setEmail("admin@mail.com");
        u.setTokenVersion(1L);
        u.setRole(UserRol.ADMIN);
        return u;
    }

    /**
     * Helper method to build a sample Patient object representing a patient user. This patient has a predefined ID, name, email, token version, role, and is associated with a User object representing the admin. This method is used in multiple tests to provide a consistent patient object for token generation.
     * @return A Patient object with patient role and predefined attributes for testing purposes, including a reference to an admin user.
     */
    private Patient buildPatient() {
        User u = buildUser();
        Patient p = new Patient();
        p.setId(10L);
        p.setName("patient1");
        p.setEmail("patient@mail.com");
        p.setTokenVersion(2L);
        p.setRole(UserRol.PATIENT);
        p.setUser(u);
        return p;
    }

    // ─── generateUserToken ────────────────────────────────────────────────────

    /**
     * Test that generateUserToken returns a non-null and non-empty JWT token.
     * Verifies that the token generation produces a valid string for an admin user.
     */
    @Test
    void generateUserToken_returnsNonNullToken() {
        String token = jwtUtil.generateUserToken("admin", buildUser(), "UTC");
        assertThat(token).isNotNull().isNotEmpty();
    }

    /**
     * Test that the subject of the generated user token matches the provided username.
     * Verifies that extracting the subject from the JWT returns the original username.
     */
    @Test
    void generateUserToken_subjectIsUsername() {
        String token = jwtUtil.generateUserToken("admin", buildUser(), "UTC");
        String subject = jwtUtil.extractUsername(token);
        assertThat(subject).isEqualTo("admin");
    }

    /**
     * Test that the generated user token contains a "role" claim with the correct value.
     * Verifies that the ADMIN role is embedded as a claim in the JWT payload.
     */
    @Test
    void generateUserToken_containsRoleClaim() {
        String token = jwtUtil.generateUserToken("admin", buildUser(), "UTC");
        String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
        assertThat(role).isEqualTo("ADMIN");
    }

    /**
     * Test that the generated user token contains a "zone" claim matching the provided time zone.
     * Verifies that the time zone is correctly stored in the JWT payload.
     */
    @Test
    void generateUserToken_containsZoneClaim() {
        String token = jwtUtil.generateUserToken("admin", buildUser(), "America/Mexico_City");
        String zone = jwtUtil.extractClaim(token, claims -> claims.get("zone", String.class));
        assertThat(zone).isEqualTo("America/Mexico_City");
    }

    /**
     * Test that the generated user token contains a "userId" claim with the correct user ID.
     * Verifies that the user's ID is embedded in the JWT payload for downstream use.
     */
    @Test
    void generateUserToken_containsUserId() {
        String token = jwtUtil.generateUserToken("admin", buildUser(), "UTC");
        Integer userId = jwtUtil.extractClaim(token, claims -> claims.get("userId", Integer.class));
        assertThat(userId).isEqualTo(1);
    }

    // ─── generatePatientToken ─────────────────────────────────────────────────

    /**
     * Test that generatePatientToken returns a non-null and non-empty JWT token.
     * Verifies that the token generation produces a valid string for a patient user.
     */
    @Test
    void generatePatientToken_returnsNonNullToken() {
        String token = jwtUtil.generatePatientToken("patient1", buildPatient(), "UTC");
        assertThat(token).isNotNull().isNotEmpty();
    }

    /**
     * Test that the subject of the generated patient token matches the provided patient name.
     * Verifies that extracting the subject from the JWT returns the original patient username.
     */
    @Test
    void generatePatientToken_subjectIsUsername() {
        String token = jwtUtil.generatePatientToken("patient1", buildPatient(), "UTC");
        String subject = jwtUtil.extractUsername(token);
        assertThat(subject).isEqualTo("patient1");
    }

    /**
     * Test that the generated patient token contains a "role" claim with the PATIENT value.
     * Verifies that the patient role is correctly embedded in the JWT payload.
     */
    @Test
    void generatePatientToken_containsPatientRole() {
        String token = jwtUtil.generatePatientToken("patient1", buildPatient(), "UTC");
        String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
        assertThat(role).isEqualTo("PATIENT");
    }

    /**
     * Test that the generated patient token contains a "userAdminId" claim
     * referencing the associated admin user's ID.
     * Verifies that the parent admin user relationship is stored in the JWT.
     */
    @Test
    void generatePatientToken_containsUserAdminId() {
        String token = jwtUtil.generatePatientToken("patient1", buildPatient(), "UTC");
        Integer adminId = jwtUtil.extractClaim(token,
                claims -> claims.get("userAdminId", Integer.class));
        assertThat(adminId).isEqualTo(1);
    }

    // ─── extractUsername ──────────────────────────────────────────────────────

    /**
     * Test that extractUsername returns the correct subject from a valid JWT token.
     * Verifies the round-trip of generating a token and extracting the username.
     */
    @Test
    void extractUsername_validToken_returnsCorrectSubject() {
        String token = jwtUtil.generateUserToken("testUser", buildUser(), "UTC");
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testUser");
    }

    /**
     * Test that extractUsername throws an exception when given an invalid token.
     * Verifies that malformed JWT strings are rejected during parsing.
     */
    @Test
    void extractUsername_invalidToken_throwsException() {
        assertThatThrownBy(() -> jwtUtil.extractUsername("invalid.token.here"))
                .isInstanceOf(Exception.class);
    }

    // ─── extractClaim ─────────────────────────────────────────────────────────

    /**
     * Test that the expiration claim of a generated token is set in the future.
     * Verifies that the token's expiration date is after the current time,
     * confirming the configured expiration duration is applied.
     */
    @Test
    void extractClaim_expirationClaim_isInFuture() {
        String token = jwtUtil.generateUserToken("admin", buildUser(), "UTC");
        java.util.Date exp = jwtUtil.extractClaim(token, Claims::getExpiration);
        assertThat(exp).isAfter(new java.util.Date());
    }
}

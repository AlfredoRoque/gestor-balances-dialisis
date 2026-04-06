package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.JwtResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.LoginRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.PatientRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.UserRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.JwtUtil;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthService}.
 * This test class covers the main authentication flows including login for both admin and patient roles,
 * email validation, password recovery, and logout functionality. Each test verifies the expected behavior
 * and interactions with the mocked dependencies, ensuring that the service handles both successful cases
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private JwtUtil jwtUtil;
    @Mock private UserRepository userRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private MailService mailService;

    @InjectMocks private AuthService authService;

    // ─── login – ADMIN ────────────────────────────────────────────────────────

    /**
     * Test successful login for an admin user.
     * Verifies that when valid credentials are provided, the service decrypts the password,
     * validates it against the stored hash, updates the token version, and returns a JWT token.
     * Also ensures that the user repository save method is called.
     */
    @Test
    void login_admin_success() {
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("encryptedPwd");
        req.setTimeZone("UTC");
        req.setRole(UserRol.ADMIN);

        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("hashed");
        user.setTokenVersion(1L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(eq("encryptedPwd"), any())).thenReturn("rawPwd");

            when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("rawPwd", "hashed")).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(jwtUtil.generateUserToken(eq("admin"), any(User.class), eq("UTC"))).thenReturn("jwt-token");

            JwtResponse response = authService.login(req);

            assertThat(response.getToken()).isEqualTo("jwt-token");
            verify(userRepository).save(any(User.class));
        }
    }

    /**
     * Test login failure when the admin user is not found.
     * Verifies that a {@link BalanceGlobalException} is thrown with a USER_NOT_FOUND message
     * when no user matches the provided username.
     */
    @Test
    void login_admin_userNotFound_throwsException() {
        LoginRequest req = new LoginRequest();
        req.setUsername("unknown");
        req.setPassword("enc");
        req.setTimeZone("UTC");
        req.setRole(UserRol.ADMIN);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(anyString(), any())).thenReturn("raw");
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(req))
                    .isInstanceOf(BalanceGlobalException.class)
                    .hasMessageContaining(Constants.USER_NOT_FOUND);
        }
    }

    /**
     * Test login failure when the admin provides an incorrect password.
     * Verifies that a {@link BalanceGlobalException} is thrown with an INVALID_CREDENTIALS message
     * when the decrypted password does not match the stored hash.
     */
    @Test
    void login_admin_invalidPassword_throwsException() {
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("enc");
        req.setTimeZone("UTC");
        req.setRole(UserRol.ADMIN);

        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("hashed");
        user.setTokenVersion(1L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(anyString(), any())).thenReturn("wrongRaw");
            when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongRaw", "hashed")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(req))
                    .isInstanceOf(BalanceGlobalException.class)
                    .hasMessageContaining(Constants.INVALID_CREDENTIALS);
        }
    }

    // ─── login – PATIENT ──────────────────────────────────────────────────────

    /**
     * Test successful login for a patient user.
     * Verifies that when valid patient credentials are provided, the service decrypts the password,
     * matches it against the stored hash, updates the token version, and returns a patient JWT token.
     */
    @Test
    void login_patient_success() {
        LoginRequest req = new LoginRequest();
        req.setUsername("patient1");
        req.setPassword("encPwd");
        req.setTimeZone("UTC");
        req.setRole(UserRol.PATIENT);

        Patient patient = new Patient();
        patient.setId(10L);
        patient.setName("patient1");
        patient.setPassword("hashedPwd");
        patient.setTokenVersion(1L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(eq("encPwd"), any())).thenReturn("rawPwd");

            when(patientRepository.findByName("patient1")).thenReturn(List.of(patient));
            when(passwordEncoder.matches("rawPwd", "hashedPwd")).thenReturn(true);
            when(patientRepository.save(any(Patient.class))).thenReturn(patient);
            when(jwtUtil.generatePatientToken(eq("patient1"), any(Patient.class), eq("UTC"))).thenReturn("patient-token");

            JwtResponse response = authService.login(req);

            assertThat(response.getToken()).isEqualTo("patient-token");
        }
    }

    /**
     * Test login failure when the patient is not found in the repository.
     * Verifies that a {@link BalanceGlobalException} is thrown with a PATIENT_NOT_FOUND message
     * when no patient matches the provided username.
     */
    @Test
    void login_patient_notFound_throwsException() {
        LoginRequest req = new LoginRequest();
        req.setUsername("ghost");
        req.setPassword("enc");
        req.setTimeZone("UTC");
        req.setRole(UserRol.PATIENT);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(anyString(), any())).thenReturn("raw");
            when(patientRepository.findByName("ghost")).thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> authService.login(req))
                    .isInstanceOf(BalanceGlobalException.class)
                    .hasMessageContaining(Constants.PATIENT_NOT_FOUND);
        }
    }

    // ─── validateMail ────────────────────────────────────────────────────────

    /**
     * Test that validateMail does not throw an exception when the email exists.
     * Verifies that when a valid email is provided, the method completes successfully
     * and the user repository is queried once.
     */
    @Test
    void validateMail_existingEmail_doesNotThrow() {
        User user = new User();
        user.setEmail("test@mail.com");
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        authService.validateMail("test@mail.com");

        verify(userRepository).findByEmail("test@mail.com");
    }

    /**
     * Test that validateMail throws an exception when the email is not found.
     * Verifies that a {@link BalanceGlobalException} with INVALID_CREDENTIALS message is thrown
     * when no user is registered with the given email address.
     */
    @Test
    void validateMail_nonExistingEmail_throwsException() {
        when(userRepository.findByEmail("no@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.validateMail("no@mail.com"))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.INVALID_CREDENTIALS);
    }

    // ─── recoverPassword ─────────────────────────────────────────────────────

    /**
     * Test that recoverPassword throws an exception when the user is not found.
     * Verifies that a {@link BalanceGlobalException} with USER_NOT_FOUND message is thrown
     * when no user is registered with the provided email address.
     */
    @Test
    void recoverPassword_userNotFound_throwsException() {
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.recoverPassword("missing@mail.com"))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.USER_NOT_FOUND);
    }

    /**
     * Test successful password recovery flow.
     * Verifies that when a valid email is provided, the service generates a temporary password,
     * encodes and saves it to the repository, and sends a recovery email to the user.
     */
    @Test
    void recoverPassword_success_sendsEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@mail.com");
        user.setPassword("oldHash");

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newHash");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(mailService).sendMailToRecoverPassword(any(User.class), anyString());

        authService.recoverPassword("user@mail.com");

        verify(userRepository).save(any(User.class));
        verify(mailService).sendMailToRecoverPassword(eq(user), anyString());
    }

    // ─── logout ──────────────────────────────────────────────────────────────

    /**
     * Test that logout increments the token version for an admin user.
     * Verifies that the token version is incremented by 1 and the updated user is persisted
     * to invalidate all existing JWT tokens for that admin.
     */
    @Test
    void logout_admin_incrementsTokenVersion() {
        User user = new User();
        user.setId(5L);
        user.setTokenVersion(3L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserRol).thenReturn(UserRol.ADMIN);
            su.when(SecurityUtils::getUserId).thenReturn(5L);
            when(userRepository.findById(5L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            authService.logout();

            assertThat(user.getTokenVersion()).isEqualTo(4L);
            verify(userRepository).save(user);
        }
    }

    /**
     * Test that logout increments the token version for a patient user.
     * Verifies that the token version is incremented by 1 and the updated patient is persisted
     * to invalidate all existing JWT tokens for that patient.
     */
    @Test
    void logout_patient_incrementsTokenVersion() {
        Patient patient = new Patient();
        patient.setId(7L);
        patient.setTokenVersion(2L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserRol).thenReturn(UserRol.PATIENT);
            su.when(SecurityUtils::getUserId).thenReturn(7L);
            when(patientRepository.findById(7L)).thenReturn(Optional.of(patient));
            when(patientRepository.save(any(Patient.class))).thenReturn(patient);

            authService.logout();

            assertThat(patient.getTokenVersion()).isEqualTo(3L);
            verify(patientRepository).save(patient);
        }
    }

    /**
     * Test that logout throws an exception when the admin user is not found.
     * Verifies that a {@link BalanceGlobalException} with USER_NOT_FOUND message is thrown
     * when the user ID retrieved from the security context does not exist in the repository.
     */
    @Test
    void logout_admin_notFound_throwsException() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserRol).thenReturn(UserRol.ADMIN);
            su.when(SecurityUtils::getUserId).thenReturn(99L);
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.logout())
                    .isInstanceOf(BalanceGlobalException.class)
                    .hasMessageContaining(Constants.USER_NOT_FOUND);
        }
    }
}


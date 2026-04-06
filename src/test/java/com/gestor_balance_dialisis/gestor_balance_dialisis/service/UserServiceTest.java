package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.UserRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserService class, covering user creation and password update scenarios.
 * Tests include successful operations as well as expected exceptions for edge cases such as duplicate email/username and incorrect current password.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    /**
     * Helper method to build a User entity with predefined values for testing purposes.
     * @param id The ID to assign to the User entity.
     * @return A User object with the specified ID and default values for other fields, including username, email, password, status, role, creation date, token version, and empty lists for patients, medicines, and vital signs.
     */
    private User buildUser(Long id) {
        User u = new User();
        u.setId(id);
        u.setUsername("admin");
        u.setEmail("admin@mail.com");
        u.setPassword("hashed");
        u.setStatus(StatusEnum.ACTIVO);
        u.setRole(UserRol.ADMIN);
        u.setCreationDate(Instant.now());
        u.setTokenVersion(1L);
        u.setPatients(new ArrayList<>());
        u.setMedicines(new ArrayList<>());
        u.setVitalSigns(new ArrayList<>());
        return u;
    }

    /**
     * Helper method to build a UserDto with predefined values for testing purposes.
     * @return A UserDto object with default values for username, email, password, and role, which can be used as input for user creation tests.
     */
    private UserDto buildUserDto() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setEmail("admin@mail.com");
        dto.setPassword("encPwd");
        dto.setRol(UserRol.ADMIN);
        return dto;
    }

    // ─── save ────────────────────────────────────────────────────────────────

    /**
     * Test successful user creation.
     * Verifies that when the email and username do not exist, the service decrypts the password,
     * encodes it, persists the user, and returns the mapped DTO with the correct username.
     */
    @Test
    void save_success() throws Exception {
        UserDto dto = buildUserDto();
        User saved = buildUser(1L);

        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPwd");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(eq("encPwd"), any())).thenReturn("rawPwd");

            UserDto result = userService.save(dto);

            assertThat(result.getUsername()).isEqualTo("admin");
            verify(userRepository).save(any(User.class));
        }
    }

    /**
     * Test that saving a user with an already registered email throws a BalanceGlobalException.
     * Verifies that an EMAIL_USER_EXIST exception is thrown when the email is already in use.
     */
    @Test
    void save_emailAlreadyExists_throwsException() {
        UserDto dto = buildUserDto();
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(buildUser(1L)));

        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.EMAIL_USER_EXIST);
    }

    /**
     * Test that saving a user with an already registered username throws a BalanceGlobalException.
     * Verifies that a USER_NAME_EXIST exception is thrown when the username is already taken.
     */
    @Test
    void save_usernameAlreadyExists_throwsException() {
        UserDto dto = buildUserDto();
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(buildUser(1L)));

        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.USER_NAME_EXIST);
    }

    // ─── updatePassword ───────────────────────────────────────────────────────

    /**
     * Test successful password update.
     * Verifies that when the current password matches, the service decrypts both passwords,
     * encodes the new one, and persists the updated user entity.
     */
    @Test
    void updatePassword_success() throws Exception {
        User user = buildUser(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newHash");
        when(userRepository.save(any(User.class))).thenReturn(user);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(eq("encActual"), any())).thenReturn("rawActual");
            su.when(() -> SecurityUtils.decryptPassword(eq("encNew"), any())).thenReturn("rawNew");

            userService.updatePassword("encActual", "encNew", 1L);

            verify(userRepository).save(any(User.class));
        }
    }

    /**
     * Test that updating password with an incorrect current password throws a BalanceGlobalException.
     * Verifies that an INVALID_CREDENTIALS exception is thrown when the current password
     * does not match the stored hash.
     */
    @Test
    void updatePassword_wrongCurrentPassword_throwsException() {
        User user = buildUser(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(eq("encActual"), any())).thenReturn("wrongRaw");

            assertThatThrownBy(() -> userService.updatePassword("encActual", "encNew", 1L))
                    .isInstanceOf(BalanceGlobalException.class)
                    .hasMessageContaining(Constants.INVALID_CREDENTIALS);
        }
    }

    /**
     * Test that updating password for a non-existent user throws a BalanceGlobalException.
     * Verifies that an UPDATE_ERROR_CREDENTIALS exception is thrown when the user ID
     * does not exist in the repository.
     */
    @Test
    void updatePassword_userNotFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword("encActual", "encNew", 99L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.UPDATE_ERROR_CREDENTIALS);
    }
}

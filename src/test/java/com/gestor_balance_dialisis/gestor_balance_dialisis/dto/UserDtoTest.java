package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the UserDto class, specifically testing the constructor that takes a User entity as input.
 * This test verifies that all relevant fields from the User entity are correctly mapped to the UserDto, except for the password field, which should not be included in the DTO for security reasons.
 */
class UserDtoTest {

    /**
     * Test that the entity-based constructor correctly maps all fields except the password.
     * Verifies that id, username, email, status, role, and creationDate are transferred from
     * the User entity, while the password is intentionally excluded for security reasons.
     */
    @Test
    void constructor_fromUser_mapsAllFieldsExceptPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setEmail("admin@mail.com");
        user.setStatus(StatusEnum.ACTIVO);
        user.setRole(UserRol.ADMIN);
        user.setPassword("secret");
        Instant now = Instant.now();
        user.setCreationDate(now);

        UserDto dto = new UserDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("admin");
        assertThat(dto.getEmail()).isEqualTo("admin@mail.com");
        assertThat(dto.getStatus()).isEqualTo(StatusEnum.ACTIVO);
        assertThat(dto.getRol()).isEqualTo(UserRol.ADMIN);
        assertThat(dto.getCreationDate()).isEqualTo(now);
        // Password must NOT be mapped for security reasons
        assertThat(dto.getPassword()).isNull();
    }
}

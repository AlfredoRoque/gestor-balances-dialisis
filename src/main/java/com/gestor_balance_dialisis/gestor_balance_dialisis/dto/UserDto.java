package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

/**
 * Data Transfer Object (DTO) for User entity, used for transferring user data between different layers of the application, such as from the service layer to the controller layer or vice versa.
 * This class includes validation annotations to ensure that the required fields are provided and in the correct format when creating or updating a user.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username email", example = "user123")
    @NotBlank(message = "Username is required")
    @NotNull(message = "Username is required")
    private String username;

    @Schema(description = "User password", example = "P@ssw0rd")
    @NotBlank(message = "Password is required")
    @NotNull(message = "Password is required")
    private String password;

    @Schema(description = "User email", example = "correo@gmail.com")
    @NotBlank(message = "Email is required")
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User status", example = "ACTIVE")
    private StatusEnum status;

    @Schema(description = "User rol", example = "ADMIN")
    private UserRol rol;

    @Schema(description = "User creation date", example = "2023-10-01T12:00:00Z")
    private Instant creationDate;

    /**
     * Constructor to create a new UserDto instance by copying the properties of a User object for response purposes, excluding the password for security reasons.
     * @param user The User object from which to copy the properties to create the UserDto instance.
     */
    public UserDto(User user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setStatus(user.getStatus());
        this.setRol(user.getRol());
        this.setCreationDate(user.getCreationDate());
    }

    /**
     * Constructor to create a new UserDto instance by copying the properties of a User object for save purposes, including a temporary password for password recovery scenarios.
     * @param user The User object from which to copy the properties to create the UserDto instance.
     * @param temporaryPassword The temporary password to be set in the UserDto instance for password
     */
    public UserDto(User user, String temporaryPassword) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setStatus(user.getStatus());
        this.setRol(user.getRol());
        this.setCreationDate(user.getCreationDate());
        this.setPassword(temporaryPassword);
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for login request.
 */
@Getter
@Setter
public class LoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Username email", example = "user123")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "User password", example = "password123")
    @NotBlank(message = "Password is required")
    private  String password;

    @Schema(description = "User time zone", example = "America/New_York")
    @NotBlank(message = "Time zone is required")
    private  String timeZone;
}

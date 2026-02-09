package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for validating email addresses in requests.
 */
@Getter
@Setter
public class ValidateMailRequestModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "User email", example = "correo@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}

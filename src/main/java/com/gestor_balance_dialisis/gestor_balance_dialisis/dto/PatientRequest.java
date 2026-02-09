package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for patient request data.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "Username email", example = "user123")
    @NotNull(message = "Username is required")
    private int age;

    @Schema(description = "Username email", example = "user123")
    @NotBlank(message = "Username is required")
    @NotNull(message = "Username is required")
    private String name;

    @Schema(description = "Patient status", example = "ACTIVO")
    private String status;

    @Schema(description = "Username email", example = "user123")
    @NotNull(message = "Username is required")
    private Long userId;

    @Schema(description = "Username email", example = "user123")
    @NotNull(message = "Username is required")
    private Long bagTypeId;
}

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
 * Data Transfer Object for Vital Sign requests.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "Vital sign name", example = "Blood Pressure")
    @NotBlank(message = "Vital sign name is required")
    @NotNull(message = "Vital sign name is required")
    private String name;
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
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
 * Data Transfer Object for updating medicine details, containing fields for dose and frequency.
 * This class is used to transfer data when updating existing medicine detail records.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDetailUpdateRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Fluid balance date", example = "1")
    private Long id;

    @Schema(description = "Medicine dose", example = "200 mg")
    @NotNull(message = "Dose is required")
    @NotBlank(message = "Dose is required")
    private String dose;

    @Schema(description = "Frequency for medicine", example = "12 hours")
    @NotNull(message = "Frequency is required")
    @NotBlank(message = "Frequency is required")
    private String frequency;

    @Schema(description = "Medicine detail status", example = "ACTIVO")
    @NotNull(message = "Status is required")
    private StatusEnum status;
}

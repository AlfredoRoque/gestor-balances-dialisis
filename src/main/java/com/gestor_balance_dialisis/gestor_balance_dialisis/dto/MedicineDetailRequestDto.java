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
import java.time.Instant;

/**
 * DTO class for creating or updating a medicine detail record for a patient.
 * This class is used to receive the necessary information to save or update a medicine detail in the system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDetailRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Fluid balance date", example = "1")
    private Long id;

    @Schema(description = "Patient id", example = "1")
    @NotNull(message = "Patient is required")
    private Long patientId;

    @Schema(description = "Registration date", example = "2023-10-01T12:00:00Z")
    @NotNull(message = "Registration date is required")
    private Instant date;

    @Schema(description = "Medicine id", example = "1")
    @NotNull(message = "Medicine is required")
    private MedicineResponse medicine;

    @Schema(description = "Medicine dose", example = "200 mg")
    @NotNull(message = "Dose is required")
    @NotBlank(message = "Dose is required")
    private String dose;

    @Schema(description = "Frequency for medicine", example = "12 hours")
    @NotNull(message = "Frequency is required")
    @NotBlank(message = "Frequency is required")
    private String frequency;

    @Schema(description = "Modification date", example = "2023-10-01T12:00:00Z")
    private Instant modificationDate;

    @Schema(description = "Deletion date", example = "2023-10-01T12:00:00Z")
    private Instant deletionDate;

    @Schema(description = "Medicine detail status", example = "ACTIVO")
    private StatusEnum status = StatusEnum.ACTIVO;
}

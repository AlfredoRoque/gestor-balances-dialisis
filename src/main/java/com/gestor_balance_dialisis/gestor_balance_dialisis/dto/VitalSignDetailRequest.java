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
import java.util.Date;

/**
 * DTO class for creating a new vital sign detail record for a patient.
 * This class is used to receive the necessary information to save a new vital sign detail in the system.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignDetailRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Vital sign detail id", example = "1")
    private Long id;

    @Schema(description = "Patient id", example = "1")
    @NotNull(message = "Patient id is required")
    private Long patient;

    @Schema(description = "Vital sign detail id date", example = "2024-06-01T12:00:00")
    @NotNull(message = "Date is required")
    private Date date;

    @Schema(description = "Vital sign id", example = "1")
    @NotNull(message = "Vital sign id is required")
    private Long vitalSign;

    @Schema(description = "Vital sign detail value", example = "120/80")
    @NotNull(message = "Value is required")
    @NotBlank(message = "Value is required")
    private String value;

    @Schema(description = "Vital sign detail status", example = "ACTIVE")
    private StatusEnum status;
}

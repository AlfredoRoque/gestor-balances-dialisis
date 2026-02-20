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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO class for updating the details of a vital sign measurement for a patient.
 * This class is used to receive the necessary information to update an existing vital sign detail record.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignDetailUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Vital sign detail id", example = "1")
    private Long id;

    @Schema(description = "Vital sign detail id date", example = "2024-06-01T12:00:00")
    @NotNull(message = "Date is required")
    private Instant date;

    @Schema(description = "Vital sign detail value", example = "120/80")
    @NotNull(message = "Value is required")
    @NotBlank(message = "Value is required")
    private String value;
}

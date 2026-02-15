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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for fluid balance request, containing information about the fluid balance record to be saved.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FluidBalanceRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Fluid balance id", example = "1")
    private Long id;

    @Schema(description = "Fluid balance date", example = "2023-10-01T12:00:00Z")
    @NotNull(message = "Fluid balance date is required")
    private LocalDateTime date;

    @Schema(description = "Liquid drained", example = "500.00")
    @NotNull(message = "Liquid drained is required")
    private BigDecimal drained;

    @Schema(description = "Liquid infused", example = "500.00")
    @NotNull(message = "Liquid infused is required")
    private BigDecimal infused;

    @Schema(description = "Patient id", example = "1")
    @NotNull(message = "Patient id is required")
    private Long patientId;

    @Schema(description = "Fluid description", example = "Description of the fluid balance record")
    @NotNull(message = "Fluid description is required")
    @NotBlank(message = "Fluid description is required")
    private String descriptionFluid;
}

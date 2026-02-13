package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for ExtraFluid entity, used for receiving extra fluid information in requests.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExtraFluidRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Extra fluid Id", example = "1")
    private Long id;

    @Schema(description = "Patient Id", example = "1")
    @NotNull(message = "Patient id is required")
    private Long patientId;

    @Schema(description = "Urine amount in milliliters", example = "500.00")
    @NotNull(message = "Urine is required")
    private BigDecimal urine;

    @Schema(description = "Ingested fluid amount in milliliters", example = "200.00")
    @NotNull(message = "Ingested is required")
    private BigDecimal ingested;

    @Schema(description = "Date of the extra fluid record", example = "2024-06-01T12:00:00Z")
    @NotNull(message = "Date is required")
    private Date date;
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.ExtraFluid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for ExtraFluid entity, used for returning extra fluid information in responses.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExtraFluidResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Extra fluid Id", example = "1")
    private Long id;

    @Schema(description = "Patient Id", example = "1")
    private Long patientId;

    @Schema(description = "Urine amount in milliliters", example = "500.00")
    private BigDecimal urine;

    @Schema(description = "Ingested fluid amount in milliliters", example = "200.00")
    private BigDecimal ingested;

    @Schema(description = "Date of the extra fluid record", example = "2024-06-01T12:00:00Z")
    private Instant date;

    /**
     * Constructor to create an ExtraFluidResponseDto from an ExtraFluid entity.
     *
     * @param extraFluid the ExtraFluid entity containing the information for the response DTO
     */
    public ExtraFluidResponseDto(ExtraFluid extraFluid) {
        this.id = extraFluid.getId();
        this.patientId = extraFluid.getPatient().getId();
        this.urine = extraFluid.getUrine();
        this.ingested = extraFluid.getIngested();
        this.date = extraFluid.getDate();
    }
}

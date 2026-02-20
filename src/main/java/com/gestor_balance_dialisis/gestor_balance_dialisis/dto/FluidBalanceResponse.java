package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidBalance;
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
 * DTO for fluid balance response, containing information about the fluid balance record to be returned in API responses.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class  FluidBalanceResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Fluid balance id", example = "1")
    private Long id;

    @Schema(description = "Fluid balance date", example = "2023-10-01T12:00:00Z")
    private Instant date;

    @Schema(description = "Liquid drained", example = "500.00")
    private BigDecimal drained;

    @Schema(description = "Liquid infused", example = "500.00")
    private BigDecimal infused;

    @Schema(description = "Patient", example = "1")
    private PatientResponse patient;

    @Schema(description = "Fluid description", example = "Description of the fluid balance record")
    private String descriptionFluid;

    private BigDecimal ultrafiltration;
    private int changeNumber;
    private BigDecimal totalUrine;
    private BigDecimal totalIngested;
    private BigDecimal partialBalance;
    private BigDecimal totalBalance;
    private String hour;
    private String day;

    /**
     * Constructor that initializes the FluidBalanceResponse object based on a FluidBalance entity.
     *
     * @param fluidBalance the FluidBalance entity from which to create the response
     */
    public FluidBalanceResponse(FluidBalance fluidBalance) {
        this.id = fluidBalance.getId();
        this.date = fluidBalance.getDate();
        this.drained = fluidBalance.getDrained();
        this.infused = fluidBalance.getInfused();
        this.patient = new PatientResponse(fluidBalance.getPatient());
        this.descriptionFluid = fluidBalance.getLiquidDescription();
    }
}

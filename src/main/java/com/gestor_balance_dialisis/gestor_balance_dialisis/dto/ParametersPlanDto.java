package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Data Transfer Object (DTO) for parameters of a plan in the dialysis balance management system.
 * This DTO encapsulates the parameters that define the limits and constraints of a plan, such as the maximum number of patients, balance, vital signs, medicines, and the history days for balance tracking.
 * It is used to transfer data between different layers of the application, particularly when creating or updating a plan.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParametersPlanDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Maximum number of patients allowed under the plan", example = "10")
    private Integer maxPatient;

    @Schema(description = "Maximum balance allowed under the plan", example = "100")
    private Integer maxBalance;

    @Schema(description = "Maximum number of vital signs allowed under the plan", example = "50")
    private Integer maxVitalSigna;

    @Schema(description = "Maximum number of medicines allowed under the plan", example = "20")
    private Integer maxMedicines;

    @Schema(description = "Days for max balance history of the plan", example = "365")
    private Integer historyDays;
}

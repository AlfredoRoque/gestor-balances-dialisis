package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for detailed information about a plan, including its id, icon, and description.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailPlanDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Id for price", example = "123")
    private Long id;

    @Schema(description = "Icon identifier", example = "icon_123")
    private String icon;

    @Schema(description = "Description of the plan detail", example = "Access to basic features")
    private String description;
}
package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing a subscription plan in the application. This class encapsulates the details of a subscription plan, including its price, name, description, and associated parameters and details.
 * It implements Serializable to allow instances of this class to be easily serialized and deserialized, which is useful for transferring data between different layers of the application or over the network.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Id for price", example = "123")
    private Long id;

    @Schema(description = "Price identifier", example = "price_123")
    private String priceId;

    @Schema(description = "Price value", example = "9.99")
    private BigDecimal price;

    @Schema(description = "Plan name", example = "Basic Plan")
    private String name;

    @Schema(description = "Plan description", example = "This is the basic plan with limited features.")
    private String description;

    @Schema(description = "Plan details", example = "[{\"feature\": \"Feature 1\", \"value\": \"Value 1\"}, {\"feature\": \"Feature 2\", \"value\": \"Value 2\"}]")
    private List<DetailPlanDto> details;

    @Schema(description = "Plan parameters", example = "{\"maxPatient\": 100, \"maxBalance\": 10}, {\"maxPatient\": 200, \"maxBalance\": 20}")
    private ParametersPlanDto parametersPlan;
}
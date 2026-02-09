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

/**
 * DTO for BagType entity, used for creating and updating bag types.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BagTypeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Bag type Id", example = "1")
    private Long id;

    @Schema(description = "Bag type name", example = "1.5")
    @NotNull(message = "Type is required")
    @NotBlank(message = "Type is required")
    private String type;

    @Schema(description = "Bag type description", example = "Description of the bag type")
    @NotNull(message = "Description is required")
    @NotBlank(message = "Description is required")
    private String description;
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.BagType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for BagType entity, used for returning bag type information in responses.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BagTypeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Bag type Id", example = "1")
    private Long id;

    @Schema(description = "Bag type name", example = "1.5")
    private String type;

    @Schema(description = "Bag type description", example = "Description of the bag type")
    private String description;

    /**
     * Constructor to create a BagTypeResponse instance from a BagType entity.
     * This allows for easy conversion from the entity model to the response DTO.
     *
     * @param bagType the BagType entity containing the information to be included in the response
     */
    public BagTypeResponse(BagType bagType) {
        this.setId(bagType.getId());
        this.setType(bagType.getType());
        this.setDescription(bagType.getDescription());
    }
}

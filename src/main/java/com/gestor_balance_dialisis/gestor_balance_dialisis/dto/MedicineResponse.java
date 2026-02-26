package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Medicine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Data Transfer Object for Medicine response.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicineResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Medicine id", example = "1")
    private Long id;

    @Schema(description = "Medicine name", example = "Aspirin")
    private String name;

    /**
     * Constructs a MedicineResponse from a Medicine entity.
     *
     * @param medicine the Medicine entity to convert
     */
    public MedicineResponse(Medicine medicine) {
        this.id = medicine.getId();
        this.name = medicine.getName();
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSign;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Data Transfer Object for Vital Sign response.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Vital sign id", example = "1")
    private Long id;

    @Schema(description = "Vital sign name", example = "Blood Pressure")
    private String name;

    /**
     * Constructs a VitalSignResponse from a VitalSign entity.
     *
     * @param vitalSign the VitalSign entity to convert
     */
    public VitalSignResponse(VitalSign vitalSign) {
        this.id = vitalSign.getId();
        this.name = vitalSign.getName();
    }
}

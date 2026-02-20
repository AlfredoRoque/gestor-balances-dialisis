package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSignDetail;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO class for creating a new vital sign detail record for a patient.
 * This class is used to receive the necessary information to save a new vital sign detail in the system.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignDetailResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Vital sign detail id", example = "1")
    private Long id;

    @Schema(description = "Patient id", example = "1")
    private Long patient;

    @Schema(description = "Vital sign detail id date", example = "2024-06-01T12:00:00")
    private Instant date;

    @Schema(description = "Vital sign id", example = "1")
    private VitalSignResponse vitalSign;

    @Schema(description = "Vital sign detail value", example = "120/80")
    private String value;

    @Schema(description = "Vital sign detail status", example = "ACTIVE")
    private StatusEnum status;

    private String day;

    /**
     * Constructor to create a VitalSignDetailResponse from a VitalSignDetail entity.
     *
     * @param vitalSignDetail The VitalSignDetail entity from which to create the response.
     */
    public VitalSignDetailResponse(VitalSignDetail vitalSignDetail) {
        this.id = vitalSignDetail.getId();
        this.patient = vitalSignDetail.getPatient().getId();
        this.date = vitalSignDetail.getDate();
        this.vitalSign = new VitalSignResponse(vitalSignDetail.getVitalSign());
        this.value = vitalSignDetail.getValue();
        this.status = vitalSignDetail.getStatus();
    }
}

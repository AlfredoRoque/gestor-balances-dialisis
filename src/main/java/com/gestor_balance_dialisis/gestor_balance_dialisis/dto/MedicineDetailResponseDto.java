package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.MedicineDetail;
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
 * DTO class for representing the details of a medicine prescribed to a patient in the response.
 * This class is used to send the necessary information about a medicine detail in the response of the API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDetailResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Fluid balance date", example = "1")
    private Long id;

    @Schema(description = "Patient id", example = "1")
    private Long patientId;

    @Schema(description = "Registration date", example = "2023-10-01T12:00:00Z")
    private Instant date;

    @Schema(description = "Medicine id", example = "1")
    private MedicineResponse medicine;

    @Schema(description = "Medicine dose", example = "200 mg")
    private String dose;

    @Schema(description = "Frequency for medicine", example = "12 hours")
    private String frequency;

    @Schema(description = "Modification date", example = "2023-10-01T12:00:00Z")
    private Instant modificationDate;

    @Schema(description = "Deletion date", example = "2023-10-01T12:00:00Z")
    private Instant deletionDate;

    @Schema(description = "Medicine detail status", example = "ACTIVO")
    private StatusEnum status;

    private String day;

    /**
     * Constructor to create a MedicineDetailResponseDto from a MedicineDetail entity.
     *
     * @param medicineDetail The MedicineDetail entity from which to create the response.
     */
    public MedicineDetailResponseDto(MedicineDetail medicineDetail) {
        this.id = medicineDetail.getId();
        this.patientId = medicineDetail.getPatient().getId();
        this.date = medicineDetail.getDate();
        this.medicine = new MedicineResponse(medicineDetail.getMedicine());
        this.dose = medicineDetail.getDose();
        this.frequency = medicineDetail.getFrequency();
        this.modificationDate = medicineDetail.getModificationDate();
        this.deletionDate = medicineDetail.getDeletionDate();
        this.status = medicineDetail.getStatus();
    }
}

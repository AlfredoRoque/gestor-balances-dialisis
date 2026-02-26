package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.MedicineDetailRequestDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Entity class representing the details of a medicine prescribed to a patient.
 * This class is mapped to the "detalle_medicina" table in the database.
 */
@Entity
@Table(name = "detalle_medicina",
        indexes = {
                @Index(name = "idx_medicine_detail_id_detalle_medicina", columnList = "id_detalle_medicina"),
                @Index(name = "idx_medicine_detail_id_paciente", columnList = "id_paciente"),
                @Index(name = "idx_medicine_detail_fecha_inicio_toma", columnList = "fecha_inicio_toma"),
                @Index(name = "idx_medicine_detail_id_medicina", columnList = "id_medicina"),
                @Index(name = "idx_medicine_detail_estatus", columnList = "estatus")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_medicina", nullable = false, unique = true)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Patient patient;

    @Column(name = "fecha_inicio_toma", nullable = false)
    private Instant date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medicina", nullable = false)
    private Medicine medicine;

    @Column(name = "dosis", nullable = false, length = 150)
    private String dose;

    @Column(name = "frecuencia", nullable = false, length = 150)
    private String frequency;

    @Column(name = "fecha_modificacion")
    private Instant modificationDate;

    @Column(name = "fecha_borrado")
    private Instant deletionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", length = 20, nullable = false)
    private StatusEnum status;

    /**
     * Constructor to create a MedicineDetail entity from a MedicineDetailRequestDto.
     *
     * @param medicineDetailRequest The MedicineDetailRequestDto containing the information to create the MedicineDetail entity.
     */
    public MedicineDetail(MedicineDetailRequestDto medicineDetailRequest) {
        this.id = medicineDetailRequest.getId();
        this.patient = new Patient(medicineDetailRequest.getPatientId());
        this.date = medicineDetailRequest.getDate();
        this.medicine = new Medicine(medicineDetailRequest.getMedicine().getId());
        this.dose = medicineDetailRequest.getDose();
        this.frequency = medicineDetailRequest.getFrequency();
        this.modificationDate = medicineDetailRequest.getModificationDate();
        this.deletionDate = medicineDetailRequest.getDeletionDate();
        this.status = StatusEnum.ACTIVO;
    }

    /**
     * Constructor to update a MedicineDetail entity based on an existing MedicineDetail and a MedicineDetailUpdateRequestDto.
     *
     * @param medicineDetail The existing MedicineDetail entity to be updated.
     * @param medicineDetailUpdateRequestDto The MedicineDetailUpdateRequestDto containing the updated information for the MedicineDetail entity.
     * @param modificationOrDeletionDate The date of modification or deletion, depending on the status provided in the update request.
     */
    public MedicineDetail(MedicineDetail medicineDetail, MedicineDetailRequestDto medicineDetailUpdateRequestDto, Instant modificationOrDeletionDate) {
        this.id = medicineDetail.getId();
        this.patient = medicineDetail.getPatient();
        this.date = medicineDetail.getDate();
        this.medicine = medicineDetail.getMedicine();
        this.dose = medicineDetailUpdateRequestDto.getDose();
        this.frequency = medicineDetailUpdateRequestDto.getFrequency();
        this.modificationDate = modificationOrDeletionDate;
        this.deletionDate = medicineDetailUpdateRequestDto.getStatus().equals(StatusEnum.INACTIVO)?modificationOrDeletionDate:null;
        this.status = medicineDetailUpdateRequestDto.getStatus();
    }
}

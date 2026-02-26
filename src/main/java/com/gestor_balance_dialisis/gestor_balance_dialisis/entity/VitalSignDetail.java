package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.VitalSignDetailRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

/**
 * Entity class representing the details of a vital sign measurement for a patient.
 * This class is mapped to the "detalle_signo_vital" table in the database.
 */
@Entity
@Table(name = "detalle_signo_vital",
        indexes = {
                @Index(name = "idx_vital_sign_detail_id_detalle_signo_vital", columnList = "id_detalle_signo_vital"),
                @Index(name = "idx_vital_sign_detail_id_paciente", columnList = "id_paciente"),
                @Index(name = "idx_vital_sign_detail_fecha", columnList = "fecha"),
                @Index(name = "idx_vital_sign_detail_id_signo_vital", columnList = "id_signo_vital"),
                @Index(name = "idx_vital_sign_detail_estatus", columnList = "estatus")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_signo_vital", nullable = false, unique = true)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Patient patient;

    @Column(name = "fecha", nullable = false)
    private Instant date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_signo_vital", nullable = false)
    private VitalSign vitalSign;

    @Column(name = "valor", nullable = false, length = 150)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", length = 20, nullable = false)
    private StatusEnum status;

    /**
     * Constructor to create a VitalSignDetail entity from a VitalSignDetailRequest DTO.
     *
     * @param vitalSignDetailRequest The request containing the information to create a new VitalSignDetail.
     */
    public VitalSignDetail(VitalSignDetailRequest vitalSignDetailRequest) {
        this.id = vitalSignDetailRequest.getId();
        this.patient = new Patient(vitalSignDetailRequest.getPatientId());
        this.date = vitalSignDetailRequest.getDate();
        this.vitalSign = new VitalSign(vitalSignDetailRequest.getVitalSign().getId());
        this.value = vitalSignDetailRequest.getValue();
        this.status = Objects.nonNull(vitalSignDetailRequest.getStatus()) ? vitalSignDetailRequest.getStatus() : StatusEnum.ACTIVO;
    }

    /**
     * Constructor to update an existing VitalSignDetail entity based on a VitalSignDetailUpdateRequest DTO.
     *
     * @param vitalSignDetail The existing VitalSignDetail entity to be updated.
     * @param vitalSignDetailUpdateRequest The request containing the new information for the update.
     */
    public VitalSignDetail(VitalSignDetail vitalSignDetail, VitalSignDetailRequest vitalSignDetailUpdateRequest) {
        this.id = vitalSignDetail.getId();
        this.patient = vitalSignDetail.getPatient();
        this.date = vitalSignDetailUpdateRequest.getDate();
        this.vitalSign = vitalSignDetail.getVitalSign();
        this.value = vitalSignDetailUpdateRequest.getValue();
        this.status = vitalSignDetail.getStatus();
    }
}

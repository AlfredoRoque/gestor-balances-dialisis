package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Entity class representing the details of a vital sign measurement for a patient.
 * This class is mapped to the "detalle_signo_vital" table in the database.
 */
@Entity
@Table(name = "detalle_signo_vital")
@Getter
@Setter
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
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_signo_vital", nullable = false)
    private VitalSign vitalSign;

    @Column(name = "valor", nullable = false, length = 150)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", length = 20, nullable = false)
    private StatusEnum status;
}

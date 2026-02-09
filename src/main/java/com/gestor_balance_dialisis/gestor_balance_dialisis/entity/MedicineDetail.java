package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Entity class representing the details of a medicine prescribed to a patient.
 * This class is mapped to the "detalle_medicina" table in the database.
 */
@Entity
@Table(name = "detalle_medicina")
@Getter
@Setter
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
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medicina", nullable = false)
    private Medicine medicine;

    @Column(name = "dosis", nullable = false, length = 150)
    private String dose;

    @Column(name = "frecuencia", nullable = false, length = 150)
    private String frequency;

    @Column(name = "fecha_modificacion", nullable = false)
    private Date modificationDate;

    @Column(name = "fecha_borrado", nullable = false)
    private Date deletionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", length = 20, nullable = false)
    private StatusEnum status;
}

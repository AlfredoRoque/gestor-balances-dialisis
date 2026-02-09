package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity class representing the "liquido_extra" table in the database.
 * This class is used to store information about extra fluids for a patient, including urine and ingested fluids.
 */
@Entity
@Table(name = "liquido_extra")
@Getter
@Setter
public class ExtraFluid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_liquido_extra", nullable = false, unique = true)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Patient patient;

    @Column(name = "orina", nullable = false)
    private BigDecimal urine;

    @Column(name = "ingerido", nullable = false)
    private BigDecimal ingested;

    @Column(name = "fecha", nullable = false)
    private Date date;
}

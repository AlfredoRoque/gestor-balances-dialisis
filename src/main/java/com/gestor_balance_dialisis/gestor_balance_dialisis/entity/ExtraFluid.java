package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ExtraFluidRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Entity class representing the "liquido_extra" table in the database.
 * This class is used to store information about extra fluids for a patient, including urine and ingested fluids.
 */
@Entity
@Table(name = "liquido_extra",
        indexes = {
                @Index(name = "idx_extra_fluid_id_liquido_extra", columnList = "id_liquido_extra"),
                @Index(name = "idx_extra_fluid_id_paciente", columnList = "id_paciente"),
                @Index(name = "idx_extra_fluid_fecha", columnList = "fecha")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Instant date;

    /**
     * Constructor to create an ExtraFluid entity from an ExtraFluidRequestDto.
     *
     * @param extraFluidRequestDto the DTO containing the information for the extra fluid record
     */
    public ExtraFluid(ExtraFluidRequestDto extraFluidRequestDto) {
        this.id = extraFluidRequestDto.getId();
        this.patient = new Patient(extraFluidRequestDto.getPatientId());
        this.urine = extraFluidRequestDto.getUrine();
        this.ingested = extraFluidRequestDto.getIngested();
        this.date = extraFluidRequestDto.getDate();
    }
}

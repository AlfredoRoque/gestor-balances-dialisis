package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.FluidBalanceRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity class representing the fluid balance of a patient.
 * This class is mapped to the "balance_liquido" table in the database.
 */
@Entity
@Table(name = "balance_liquido")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FluidBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_balence_liquido", nullable = false, unique = true)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Patient patient;

    @Column(name = "fecha", nullable = false)
    private Date date;

    @Column(name = "infundido", nullable = false)
    private BigDecimal infused;

    @Column(name = "drenado", nullable = false)
    private BigDecimal drained;

    @Column(name = "descripcion_liquido", nullable = false)
    private String liquidDescription;

    /**
     * Constructor that initializes the FluidBalance entity based on a FluidBalanceRequest DTO.
     *
     * @param fluidBalanceRequest the FluidBalanceRequest DTO containing the data
     */
    public FluidBalance(FluidBalanceRequest fluidBalanceRequest) {
        this.date = fluidBalanceRequest.getDate();
        this.drained = fluidBalanceRequest.getDrained();
        this.infused = fluidBalanceRequest.getInfused();
        this.liquidDescription = fluidBalanceRequest.getDescriptionFluid();
        this.patient = new Patient(fluidBalanceRequest.getPatientId());
    }
}

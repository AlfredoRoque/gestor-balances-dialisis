package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Entity class representing the balance liquid date, which is used to track the dates of fluid balance records.
 * This class is mapped to the "balance_liquido_fecha" table in the database.
 */
@Entity
@Table(name = "balance_liquido_fecha",
        indexes = {
                @Index(name = "idx_fluid_date_id_balence_liquido_fecha", columnList = "id_balance_liquido_fecha"),
                @Index(name = "idx_fluid_date_fecha", columnList = "fecha")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FluidDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_balance_liquido_fecha", nullable = false, unique = true)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private Instant date;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", length = 20, nullable = false)
    private StatusEnum status;
}

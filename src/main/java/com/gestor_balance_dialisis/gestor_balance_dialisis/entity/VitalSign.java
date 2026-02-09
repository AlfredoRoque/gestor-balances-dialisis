package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a vital sign in the system.
 * This class is mapped to the "signo_vital" table in the database.
 */
@Entity
@Table(name = "signo_vital")
@Getter
@Setter
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_signo_vital", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String name;
}

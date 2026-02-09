package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a medicine in the system.
 */
@Entity
@Table(name = "medicina")
@Getter
@Setter
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicina", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String name;
}

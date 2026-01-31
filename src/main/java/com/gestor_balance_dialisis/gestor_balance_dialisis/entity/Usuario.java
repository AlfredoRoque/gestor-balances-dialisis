package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id_usuario", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombre_usuario", nullable = false, length = 100)
    private String username;

    @Column(name = "contrasena", nullable = false)
    private String password;

    @Column(name = "direccion", length = 200)
    private String address;
}

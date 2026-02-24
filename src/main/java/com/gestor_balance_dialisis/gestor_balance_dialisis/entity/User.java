package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity class representing a user in the system.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombre_usuario", nullable = false, length = 100, unique = true)
    private String username;

    @Column(name = "contrasena", nullable = false)
    private String password;

    @Column(name = "correo", length = 80, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", length = 20, nullable = false)
    private StatusEnum status;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 20, nullable = false)
    private UserRol rol;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant creationDate;

    @Column(name = "sesion_version", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long tokenVersion;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Patient> patients = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medicine> medicines = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VitalSign> vitalSigns = new ArrayList<>();

    /**
     * Constructor to create a new User instance by copying the properties of another User object.
     * @param user The User object from which to copy the properties.
     * @param encodedPassword The encoded password to be set for the new User instance.
     */
    public User(UserDto user, String encodedPassword) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setRol(user.getRol());
        this.setStatus(StatusEnum.ACTIVO);
        this.setRol(UserRol.USER);
        this.setCreationDate(Instant.now());
        this.setPassword(encodedPassword);
    }

    /**
     * Constructor to create a new User instance with only the user ID.
     * @param userId The ID of the user to be created.
     */
    public User(Long userId) {
        this.setId(userId);
    }
}

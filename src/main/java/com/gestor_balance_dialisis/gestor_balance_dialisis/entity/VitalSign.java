package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.VitalSignRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing a vital sign in the system.
 * This class is mapped to the "signo_vital" table in the database.
 */
@Entity
@Table(name = "signo_vital")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_signo_vital", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String name;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;

    /**
     * Constructs a VitalSign entity from a VitalSignRequest DTO.
     *
     * @param vitalSignRequest the request containing the vital sign information
     */
    public VitalSign(VitalSignRequest vitalSignRequest) {
        this.id = vitalSignRequest.getId();
        this.name = vitalSignRequest.getName();
        this.user = new User(vitalSignRequest.getUserId());
    }

    /**
     * Constructs a VitalSign entity with only the id.
     *
     * @param vitalSign the id of the vital sign
     */
    public VitalSign(@NotNull(message = "Vital sign id is required") Long vitalSign) {
        this.id = vitalSign;
    }
}

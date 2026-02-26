package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.MedicineRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing a medicine in the system.
 */
@Entity
@Table(name = "medicina",
        indexes = {
                @Index(name = "idx_medicine_id_medicina", columnList = "id_medicina"),
                @Index(name = "idx_medicine_id_usuario", columnList = "id_usuario")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicina", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String name;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;


    /**
     * Constructs a Medicine entity from a MedicineRequest DTO.
     *
     * @param medicineRequest the MedicineRequest DTO containing the data to create the Medicine entity
     */
    public Medicine(MedicineRequest medicineRequest) {
        this.id = medicineRequest.getId();
        this.name = medicineRequest.getName();
        this.user = new User(medicineRequest.getUserId());
    }

    /**
     * Constructs a Medicine entity with only the ID.
     *
     * @param medicineId the ID of the medicine
     */
    public Medicine(@NotNull(message = "") Long medicineId) {
        this.id = medicineId;
    }
}

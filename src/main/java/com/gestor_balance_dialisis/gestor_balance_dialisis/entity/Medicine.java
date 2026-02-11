package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.MedicineRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing a medicine in the system.
 */
@Entity
@Table(name = "medicina")
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

    /**
     * Constructs a Medicine entity from a MedicineRequest DTO.
     *
     * @param medicineRequest the MedicineRequest DTO containing the data to create the Medicine entity
     */
    public Medicine(MedicineRequest medicineRequest) {
        this.id = medicineRequest.getId();
        this.name = medicineRequest.getName();
    }
}

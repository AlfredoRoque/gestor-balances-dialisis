package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.BagTypeRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing the "tipo_bolsa" table in the database.
 * This class is used to store information about different types of bags used in dialysis.
 */
@Entity
@Table(name = "tipo_bolsa")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BagType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_bolsa", nullable = false, unique = true)
    private Long id;

    @Column(name = "tipo", nullable = false, length = 50)
    private String type;

    @Column(name = "decripcion")
    private String description;

    /**
     * Constructor to create a BagType instance with only the ID.
     * This can be useful when you want to reference an existing BagType without needing to load all its details.
     *
     * @param id the ID of the BagType
     */
    public BagType(Long id) {
        this.setId(id);
    }

    /**
     * Constructor to create a BagType instance from a BagTypeRequest DTO.
     * This allows for easy conversion from the request data to the entity model.
     *
     * @param bagTypeRequest the request containing the bag type information
     */
    public BagType(BagTypeRequest bagTypeRequest) {
        this.setId(bagTypeRequest.getId());
        this.setType(bagTypeRequest.getType());
        this.setDescription(bagTypeRequest.getDescription());
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * PatientResponse is a Data Transfer Object (DTO) that represents the response structure for patient-related operations.
 * It contains fields such as id, age, name, userId, bagTypeId, and status. The class includes constructors,
 * getters, and setters for these fields. It also implements Serializable for object serialization.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Patient Id", example = "1")
    private Long id;

    @Schema(description = "Patient age", example = "30")
    private int age;

    @Schema(description = "Patient name", example = "John Doe")
    private String name;

    @Schema(description = "Patient user id", example = "1")
    private Long userId;

    @Schema(description = "Patient bag type", example = "{\"id\": 1, \"type\": \"1.5\", \"description\": \"Description of the bag type\"}")
    private BagTypeResponse bagType;

    @Schema(description = "Patient status", example = "ACTIVO")
    private String status;

    @Schema(description = "Patient email", example = "John_Doe@gmail.com")
    private String email;

    /**
     * Constructs a PatientResponse object based on a Patient entity.
     *
     * @param patient the Patient entity from which to create the response
     */
    public PatientResponse(Patient patient) {
        this.setId(patient.getId());
        this.setAge(patient.getAge());
        this.setName(patient.getName());
        this.setStatus(patient.getStatus().name());
        this.setUserId(Objects.nonNull(patient.getUser())?patient.getUser().getId():null);
        this.setBagType(Objects.nonNull(patient.getBagType())?new BagTypeResponse(patient.getBagType()):null) ;
        this.setEmail(patient.getEmail());
    }
}

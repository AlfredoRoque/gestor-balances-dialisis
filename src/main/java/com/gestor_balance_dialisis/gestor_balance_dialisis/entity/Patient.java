package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PatientRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity class representing a patient in the dialysis balance management system.
 * This class is mapped to the "paciente" table in the database.
 */
@Entity
@Table(name = "paciente")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String name;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;

    @Column(name = "edad", nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", nullable = false)
    private StatusEnum status = StatusEnum.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_bolsa", nullable = false)
    private BagType bagType;

    @JsonManagedReference
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FluidBalance> fluidBalances = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExtraFluid> extraFluids = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VitalSignDetail> vitalSignDetails = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineDetail> medicineDetails = new ArrayList<>();

    /**
     * Constructor to create a Patient entity from a PatientRequest DTO.
     *
     * @param patientRequest the PatientRequest DTO containing the patient's information
     */
    public Patient(PatientRequest patientRequest) {
        this.setId(patientRequest.getId());
        this.setAge(patientRequest.getAge());
        this.setName(patientRequest.getName());
        this.setStatus(Objects.nonNull(patientRequest.getStatus())?StatusEnum.valueOf(patientRequest.getStatus()):this.status);
        this.setUser(new User(patientRequest.getUserId()));
        this.setBagType(new BagType(patientRequest.getBagTypeId()));
    }

    /**
     * Constructor to create a Patient entity with only the patient ID.
     *
     * @param patientId the ID of the patient
     */
    public Patient(@NotNull(message = "Patient id is required") Long patientId) {
        this.id = patientId;
    }
}

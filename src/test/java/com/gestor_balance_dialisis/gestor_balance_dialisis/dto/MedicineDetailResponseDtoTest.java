package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the MedicineDetailResponseDto class, focusing on the constructor that maps from a MedicineDetail entity.
 * This test verifies that all relevant fields from the MedicineDetail entity are correctly transferred to the DTO, including nested objects like the Medicine response. It ensures that the DTO accurately represents the data from the entity, which is crucial for the integrity of the API responses that utilize this DTO.
 */
class MedicineDetailResponseDtoTest {

    /**
     * Test that the entity-based constructor correctly maps all fields from a MedicineDetail entity.
     * Verifies that id, patientId, dose, frequency, status, and the nested medicine response
     * are properly transferred from the entity to the DTO.
     */
    @Test
    void constructor_fromEntity_mapsAllFields() {
        Medicine medicine = new Medicine();
        medicine.setId(1L);
        medicine.setName("Aspirin");

        Patient patient = new Patient(2L);

        MedicineDetail detail = new MedicineDetail();
        detail.setId(7L);
        detail.setPatient(patient);
        detail.setMedicine(medicine);
        detail.setDose("200 mg");
        detail.setFrequency("12h");
        detail.setDate(Instant.parse("2024-06-01T08:00:00Z"));
        detail.setStatus(StatusEnum.ACTIVO);

        MedicineDetailResponseDto dto = new MedicineDetailResponseDto(detail);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getPatientId()).isEqualTo(2L);
        assertThat(dto.getDose()).isEqualTo("200 mg");
        assertThat(dto.getFrequency()).isEqualTo("12h");
        assertThat(dto.getStatus()).isEqualTo(StatusEnum.ACTIVO);
        assertThat(dto.getMedicine().getName()).isEqualTo("Aspirin");
    }
}

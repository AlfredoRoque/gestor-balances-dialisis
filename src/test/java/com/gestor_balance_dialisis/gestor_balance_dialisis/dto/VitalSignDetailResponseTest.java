package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the VitalSignDetailResponse DTO, focusing on the constructor that maps from a VitalSignDetail entity.
 * These tests ensure that all relevant fields from the entity are correctly transferred to the DTO, including nested objects and enum values.
 */
class VitalSignDetailResponseTest {

    /**
     * Test that the entity-based constructor correctly maps all fields from a VitalSignDetail entity.
     * Verifies that id, patient ID, value, status, and the nested vitalSign response
     * are properly transferred from the entity to the DTO.
     */
    @Test
    void constructor_fromEntity_mapsAllFields() {
        VitalSign vs = new VitalSign();
        vs.setId(1L);
        vs.setName("Blood Pressure");

        Patient patient = new Patient(2L);

        VitalSignDetail detail = new VitalSignDetail();
        detail.setId(5L);
        detail.setPatient(patient);
        detail.setVitalSign(vs);
        detail.setValue("120/80");
        detail.setDate(Instant.parse("2024-06-01T10:00:00Z"));
        detail.setStatus(StatusEnum.ACTIVO);

        VitalSignDetailResponse response = new VitalSignDetailResponse(detail);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getPatient()).isEqualTo(2L);
        assertThat(response.getValue()).isEqualTo("120/80");
        assertThat(response.getStatus()).isEqualTo(StatusEnum.ACTIVO);
        assertThat(response.getVitalSign().getName()).isEqualTo("Blood Pressure");
    }
}

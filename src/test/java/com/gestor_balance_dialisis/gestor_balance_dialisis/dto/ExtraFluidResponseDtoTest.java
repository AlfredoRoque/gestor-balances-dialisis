package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.ExtraFluid;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the ExtraFluidResponseDto class, focusing on the constructor that maps from an ExtraFluid entity and the default no-args constructor.
 * These tests verify that the DTO correctly initializes its fields based on the provided entity and that the default constructor sets fields to null as expected.
 */
class ExtraFluidResponseDtoTest {

    /**
     * Test that the entity-based constructor correctly maps all fields from an ExtraFluid entity.
     * Verifies that id, patientId, urine, ingested, and date are properly transferred to the DTO.
     */
    @Test
    void constructor_fromEntity_mapsAllFields() {
        Patient patient = new Patient(1L);

        ExtraFluid ef = new ExtraFluid();
        ef.setId(5L);
        ef.setPatient(patient);
        ef.setUrine(new BigDecimal("300.00"));
        ef.setIngested(new BigDecimal("150.00"));
        Instant date = Instant.parse("2024-06-01T12:00:00Z");
        ef.setDate(date);

        ExtraFluidResponseDto dto = new ExtraFluidResponseDto(ef);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getPatientId()).isEqualTo(1L);
        assertThat(dto.getUrine()).isEqualByComparingTo("300.00");
        assertThat(dto.getIngested()).isEqualByComparingTo("150.00");
        assertThat(dto.getDate()).isEqualTo(date);
    }

    /**
     * Test that the no-args constructor initializes all fields to null.
     * Verifies the default state of the DTO when created without arguments.
     */
    @Test
    void noArgs_constructor_fieldsAreNull() {
        ExtraFluidResponseDto dto = new ExtraFluidResponseDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getUrine()).isNull();
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the FluidBalanceResponse DTO, focusing on the constructor that maps from a FluidBalance entity.
 * These tests verify that all relevant fields from the FluidBalance entity are correctly transferred to the DTO, including nested patient information. Additionally, tests ensure that the no-args constructor initializes fields to null as expected.
 */
class FluidBalanceResponseTest {

    /**
     * Helper method to build a sample FluidBalance entity with nested Patient, User, and BagType for testing purposes.
     * @return A fully populated FluidBalance entity with associated Patient, User, and BagType data for use in tests.
     */
    private FluidBalance buildFluidBalance() {
        User u = new User();
        u.setId(1L);

        BagType bt = new BagType();
        bt.setId(1L);
        bt.setType("1.5");

        Patient p = new Patient();
        p.setId(1L);
        p.setName("John");
        p.setAge(40);
        p.setEmail("john@mail.com");
        p.setStatus(StatusEnum.ACTIVO);
        p.setUser(u);
        p.setBagType(bt);

        FluidBalance fb = new FluidBalance();
        fb.setId(10L);
        fb.setPatient(p);
        fb.setDate(Instant.parse("2024-06-01T08:00:00Z"));
        fb.setDrained(new BigDecimal("500.00"));
        fb.setInfused(new BigDecimal("500.00"));
        fb.setLiquidDescription("Peritoneal");
        return fb;
    }

    /**
     * Test that the entity-based constructor correctly maps all fields from a FluidBalance entity.
     * Verifies that id, drained, infused, descriptionFluid, and the nested patient response
     * are properly transferred from the entity to the DTO.
     */
    @Test
    void constructor_fromEntity_mapsAllFields() {
        FluidBalance fb = buildFluidBalance();

        FluidBalanceResponse response = new FluidBalanceResponse(fb);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getDrained()).isEqualByComparingTo("500.00");
        assertThat(response.getInfused()).isEqualByComparingTo("500.00");
        assertThat(response.getDescriptionFluid()).isEqualTo("Peritoneal");
        assertThat(response.getPatient()).isNotNull();
        assertThat(response.getPatient().getName()).isEqualTo("John");
    }

    /**
     * Test that the no-args constructor initializes all fields to null.
     * Verifies the default state of the DTO when created without arguments.
     */
    @Test
    void noArgs_constructor_fieldsAreNull() {
        FluidBalanceResponse dto = new FluidBalanceResponse();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getDrained()).isNull();
    }
}

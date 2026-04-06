package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.BagType;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the PatientResponse DTO, focusing on the constructor that maps from a Patient entity.
 * These tests verify that all relevant fields from the Patient entity are correctly transferred to the DTO,
 * including handling of null values for associated entities like User and BagType.
 */
class PatientResponseTest {

    /**
     * Helper method to build a sample Patient entity with all fields populated, including associated User and BagType.
     * @return A fully populated Patient entity for testing purposes.
     */
    private Patient buildPatient() {
        User u = new User();
        u.setId(1L);

        BagType bt = new BagType();
        bt.setId(1L);
        bt.setType("1.5");
        bt.setDescription("Standard");

        Patient p = new Patient();
        p.setId(10L);
        p.setName("John");
        p.setAge(40);
        p.setStatus(StatusEnum.ACTIVO);
        p.setEmail("john@mail.com");
        p.setUser(u);
        p.setBagType(bt);
        return p;
    }

    /**
     * Test that the entity-based constructor correctly maps all fields from a Patient entity.
     * Verifies that id, name, age, status, email, userId, and the nested bagType response
     * are properly transferred from the entity to the DTO.
     */
    @Test
    void constructor_fromPatient_mapsAllFields() {
        Patient patient = buildPatient();

        PatientResponse response = new PatientResponse(patient);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo("John");
        assertThat(response.getAge()).isEqualTo(40);
        assertThat(response.getStatus()).isEqualTo("ACTIVO");
        assertThat(response.getEmail()).isEqualTo("john@mail.com");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getBagType()).isNotNull();
        assertThat(response.getBagType().getType()).isEqualTo("1.5");
    }

    /**
     * Test that the constructor handles a null BagType by setting the bagType field to null.
     * Verifies that the DTO does not throw an exception when the patient has no bag type assigned.
     */
    @Test
    void constructor_fromPatient_nullBagType_setsNullBagType() {
        Patient patient = buildPatient();
        patient.setBagType(null);

        PatientResponse response = new PatientResponse(patient);

        assertThat(response.getBagType()).isNull();
    }

    /**
     * Test that the constructor handles a null User by setting the userId field to null.
     * Verifies that the DTO does not throw an exception when the patient has no user assigned.
     */
    @Test
    void constructor_fromPatient_nullUser_setsNullUserId() {
        Patient patient = buildPatient();
        patient.setUser(null);

        PatientResponse response = new PatientResponse(patient);

        assertThat(response.getUserId()).isNull();
    }
}

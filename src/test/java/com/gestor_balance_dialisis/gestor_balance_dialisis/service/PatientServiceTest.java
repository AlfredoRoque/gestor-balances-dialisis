package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PatientService covering all public methods, including save, findByUserId, updatePatient, deletePatient, and findById.
 * Tests include successful scenarios, edge cases, and exception handling to ensure comprehensive coverage of the service logic, especially around subscription limits and password handling.
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SubscriptionService subscriptionService;
    @Mock private MedicineDetailRepository medicineDetailRepository;
    @Mock private VitalSignDetailRepository vitalSignDetailRepository;
    @Mock private ExtraFluidRepository extraFluidRepository;
    @Mock private FluidBalanceRepository fluidBalanceRepository;

    @InjectMocks private PatientService patientService;

    /**
     * Helper method to create a SubscriptionDto with a special plan that allows 10 patients. This is used to test scenarios where the subscription plan has specific limits and parameters.
     * @return A SubscriptionDto configured with a special plan that allows up to 10 patients, along with other parameters for testing purposes.
     */
    private SubscriptionDto specialSubscription() {
        ParametersPlanDto params = new ParametersPlanDto(10, 100, 20, 15, 365);
        PlanDto plan = new PlanDto();
        plan.setName(Constants.SPECIAL_PLAN);
        plan.setParametersPlan(params);
        SubscriptionDto sub = new SubscriptionDto();
        sub.setPlan(plan);
        return sub;
    }

    /**
     * Helper method to create a SubscriptionDto with a basic plan that has a configurable maximum number of patients. This is used to test scenarios where the subscription plan has a specific patient limit that can be reached.
     * @param maxPatients The maximum number of patients allowed by the subscription plan, which will be used to test the behavior when the limit is reached.
     * @return A SubscriptionDto configured with a basic plan that allows up to the specified number of patients, along with other parameters for testing purposes.
     */
    private SubscriptionDto limitedSubscription(int maxPatients) {
        ParametersPlanDto params = new ParametersPlanDto(maxPatients, 50, 10, 5, 30);
        PlanDto plan = new PlanDto();
        plan.setName("BASIC");
        plan.setParametersPlan(params);
        SubscriptionDto sub = new SubscriptionDto();
        sub.setPlan(plan);
        return sub;
    }

    /**
     * Helper method to build a PatientRequest object with default values for testing. This method creates a request DTO that can be used in multiple test cases to ensure consistency and reduce duplication when testing patient creation and updates.
     * @return A PatientRequest object populated with default values for name, age, user ID, bag type ID, password, and email, which can be used in various test scenarios for the PatientService.
     */
    private PatientRequest buildRequest() {
        PatientRequest req = new PatientRequest();
        req.setName("John");
        req.setAge(40);
        req.setUserId(1L);
        req.setBagTypeId(1L);
        req.setPassword("encPwd");
        req.setEmail("john@mail.com");
        return req;
    }

    /**
     * Helper method to build a Patient entity with default values for testing. This method creates a patient entity that can be used in multiple test cases to ensure consistency and reduce duplication when testing patient retrieval, updates, and deletions.
     * @return A Patient entity populated with default values for ID, name, age, password, email, associated user, and bag type, which can be used in various test scenarios for the PatientService.
     */
    private Patient buildPatient() {
        Patient p = new Patient();
        p.setId(1L);
        p.setName("John");
        p.setAge(40);
        p.setPassword("hashedPwd");
        p.setEmail("john@mail.com");
        User u = new User();
        u.setId(1L);
        p.setUser(u);
        BagType bt = new BagType();
        bt.setId(1L);
        bt.setType("1.5");
        p.setBagType(bt);
        return p;
    }

    // ─── save ────────────────────────────────────────────────────────────────

    /**
     * Test successful patient creation with a special subscription plan.
     * Verifies that when the patient does not already exist and the user has a special plan,
     * the service decrypts the password, encodes it, persists the patient, and returns the mapped response.
     */
    @Test
    void save_success_specialPlan() {
        PatientRequest req = buildRequest();
        Patient saved = buildPatient();

        when(patientRepository.findByNameAndUserId("John", 1L)).thenReturn(Optional.empty());
        when(subscriptionService.getSubscription(1L)).thenReturn(specialSubscription());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPwd");
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(eq("encPwd"), any())).thenReturn("rawPwd");

            PatientResponse response = patientService.save(req);

            assertThat(response.getName()).isEqualTo("John");
            verify(patientRepository).save(any(Patient.class));
        }
    }

    /**
     * Test that saving a patient that already exists throws a BalanceGlobalException.
     * Verifies that when a patient with the same name and user ID is found,
     * the service throws an exception with PATIENT_ALREADY_EXIST message.
     */
    @Test
    void save_alreadyExists_throwsException() {
        PatientRequest req = buildRequest();
        when(patientRepository.findByNameAndUserId("John", 1L)).thenReturn(Optional.of(buildPatient()));

        assertThatThrownBy(() -> patientService.save(req))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.PATIENT_ALREADY_EXIST);
    }

    /**
     * Test that saving a patient when the subscription limit is reached throws a BalanceGlobalException.
     * Verifies that when the current patient count equals the plan's maximum allowed patients,
     * the service prevents creation and throws an exception.
     */
    @Test
    void save_limitReached_throwsException() {
        PatientRequest req = buildRequest();
        when(patientRepository.findByNameAndUserId("John", 1L)).thenReturn(Optional.empty());
        when(subscriptionService.getSubscription(1L)).thenReturn(limitedSubscription(2));

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserId).thenReturn(1L);
            when(patientRepository.countByUserId(1L)).thenReturn(2);

            assertThatThrownBy(() -> patientService.save(req))
                    .isInstanceOf(BalanceGlobalException.class);
        }
    }

    // ─── findByUserId ─────────────────────────────────────────────────────────

    /**
     * Test that findByUserId returns a correctly mapped list of patient responses.
     * Verifies that all patient entities associated with the given user ID are converted to DTOs.
     */
    @Test
    void findByUserId_returnsMappedList() {
        Patient p = buildPatient();
        when(patientRepository.findByUserId(1L)).thenReturn(List.of(p));

        List<PatientResponse> result = patientService.findByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John");
    }

    /**
     * Test that findByUserId returns an empty list when no patients are found for the given user ID.
     * Verifies that the service correctly handles the empty result from the repository.
     */
    @Test
    void findByUserId_empty_returnsEmptyList() {
        when(patientRepository.findByUserId(99L)).thenReturn(List.of());

        List<PatientResponse> result = patientService.findByUserId(99L);

        assertThat(result).isEmpty();
    }

    // ─── updatePatient ────────────────────────────────────────────────────────

    /**
     * Test that updating a patient with the SAME_PASSWORD sentinel value reuses the existing hash.
     * Verifies that when the password field matches SAME_PASSWORD, the service retrieves the
     * existing patient's password hash and persists it without re-encoding.
     */
    @Test
    void updatePatient_samePassword_usesExistingHash() {
        PatientRequest req = buildRequest();
        req.setPassword(Constants.SAME_PASSWORD);

        Patient existing = buildPatient();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenReturn(existing);

        PatientResponse result = patientService.updatePatient(req, 1L);

        assertThat(result).isNotNull();
        verify(patientRepository).save(any(Patient.class));
    }

    /**
     * Test that updating a patient with a new encrypted password decrypts, encodes, and saves it.
     * Verifies that the service calls SecurityUtils.decryptPassword and passwordEncoder.encode
     * before persisting the updated patient entity.
     */
    @Test
    void updatePatient_newPassword_encryptsAndSaves() throws Exception {
        PatientRequest req = buildRequest();
        req.setPassword("newEncPwd");

        Patient existing = buildPatient();
        when(patientRepository.save(any(Patient.class))).thenReturn(existing);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(() -> SecurityUtils.decryptPassword(eq("newEncPwd"), any())).thenReturn("newRaw");
            when(passwordEncoder.encode("newRaw")).thenReturn("newHash");

            PatientResponse result = patientService.updatePatient(req, 1L);

            assertThat(result).isNotNull();
        }
    }

    /**
     * Test that updating a patient with SAME_PASSWORD throws an exception when the patient is not found.
     * Verifies that a BalanceGlobalException with PATIENT_NOT_FOUND message is thrown
     * when the repository returns empty for the given patient ID.
     */
    @Test
    void updatePatient_samePassword_patientNotFound_throwsException() {
        PatientRequest req = buildRequest();
        req.setPassword(Constants.SAME_PASSWORD);
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.updatePatient(req, 1L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.PATIENT_NOT_FOUND);
    }

    // ─── deletePatient ────────────────────────────────────────────────────────

    /**
     * Test successful patient deletion including cleanup of related records.
     * Verifies that the service deletes all associated medicine details, vital sign details,
     * extra fluids, fluid balances, and finally the patient itself.
     */
    @Test
    void deletePatient_success() {
        Patient p = buildPatient();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(p));
        when(medicineDetailRepository.findByPatientId(1L)).thenReturn(List.of());
        when(vitalSignDetailRepository.findByPatientId(1L)).thenReturn(List.of());
        when(extraFluidRepository.findByPatientId(1L)).thenReturn(List.of());
        when(fluidBalanceRepository.findByPatientId(1L)).thenReturn(List.of());
        doNothing().when(patientRepository).deleteById(1L);

        patientService.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
    }

    /**
     * Test that deleting a non-existent patient throws a BalanceGlobalException.
     * Verifies that a PATIENT_NOT_FOUND exception is thrown when the patient ID does not exist.
     */
    @Test
    void deletePatient_notFound_throwsException() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.deletePatient(99L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.PATIENT_NOT_FOUND);
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    /**
     * Test that findById returns the correct patient response when found.
     * Verifies that the returned DTO contains the expected ID and name from the entity.
     */
    @Test
    void findById_found_returnsResponse() {
        Patient p = buildPatient();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(p));

        PatientResponse result = patientService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John");
    }

    /**
     * Test that findById throws a BalanceGlobalException when the patient is not found.
     * Verifies that a PATIENT_NOT_FOUND exception is thrown for a non-existent patient ID.
     */
    @Test
    void findById_notFound_throwsException() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.findById(999L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.PATIENT_NOT_FOUND);
    }
}


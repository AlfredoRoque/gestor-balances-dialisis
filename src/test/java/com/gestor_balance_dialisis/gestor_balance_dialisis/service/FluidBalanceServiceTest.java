package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidBalance;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FluidBalanceService covering save, update, delete, retrieval by date, and cleanup logic.
 * Tests include successful operations and expected exceptions for edge cases such as missing patient, duplicate entries, and subscription limits. Mocked dependencies include repositories and subscription service.
 * Utility methods are used to build common test data and to mock static utility calls for date handling and security context. Each test verifies both the expected outcome and the interactions with mocked dependencies to ensure correct service behavior.
 */
@ExtendWith(MockitoExtension.class)
class FluidBalanceServiceTest {

    @Mock private FluidBalanceRepository fluidBalanceRepository;
    @Mock private ExtraFluidRepository extraFluidRepository;
    @Mock private VitalSignDetailRepository vitalSignDetailRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private SubscriptionService subscriptionService;

    @InjectMocks private FluidBalanceService fluidBalanceService;

    /**
     * Helper method to create a SubscriptionDto with a special plan configuration.
     * @return SubscriptionDto configured with a special plan that has unlimited retention and high limits, used for testing scenarios where the subscription should not restrict fluid balance operations.
     */
    private SubscriptionDto specialSub() {
        ParametersPlanDto params = new ParametersPlanDto(10, 100, 20, 15, 365);
        PlanDto plan = new PlanDto();
        plan.setName(Constants.SPECIAL_PLAN);
        plan.setParametersPlan(params);
        SubscriptionDto sub = new SubscriptionDto();
        sub.setPlan(plan);
        return sub;
    }

    /**
     * Helper method to build a Patient entity for testing purposes.
     * @return Patient entity with a linked User, used for testing fluid balance operations that require patient information. The patient has a fixed ID and name for consistency across tests.
     */
    private Patient buildPatient() {
        User u = new User();
        u.setId(1L);
        Patient p = new Patient();
        p.setId(1L);
        p.setName("John");
        p.setUser(u);
        return p;
    }

    /**
     * Helper method to build a FluidBalance entity for testing purposes.
     * @param id The ID to assign to the FluidBalance entity, allowing for differentiation in tests that require multiple instances. The entity is populated with fixed values for drained, infused, date, and description to ensure consistency across tests that involve fluid balance data.
     * @return FluidBalance entity with the specified ID and preset values, used for testing retrieval, update, and deletion operations in the FluidBalanceService.
     */
    private FluidBalance buildFluidBalance(Long id) {
        FluidBalance fb = new FluidBalance();
        fb.setId(id);
        fb.setPatient(buildPatient());
        fb.setDrained(new BigDecimal("500.00"));
        fb.setInfused(new BigDecimal("500.00"));
        fb.setDate(Instant.parse("2024-06-01T08:00:00Z"));
        fb.setLiquidDescription("Peritoneal");
        return fb;
    }

    /**
     * Helper method to build a FluidBalanceRequest DTO for testing purposes.
     * @return FluidBalanceRequest DTO populated with fixed values for patient ID, date, drained, infused, and description. This request object is used in tests that involve creating or updating fluid balance records, ensuring consistency in the input data across different test scenarios.
     */
    private FluidBalanceRequest buildRequest() {
        FluidBalanceRequest req = new FluidBalanceRequest();
        req.setPatientId(1L);
        req.setDate(Instant.parse("2024-06-01T08:00:00Z"));
        req.setDrained(new BigDecimal("500.00"));
        req.setInfused(new BigDecimal("500.00"));
        req.setDescriptionFluid("Peritoneal");
        return req;
    }

    // ─── save ────────────────────────────────────────────────────────────────

    /**
     * Test successful fluid balance creation with a special plan and no duplicate dates.
     * Verifies that when the patient exists, has a special plan, and no duplicate date entry,
     * the service persists the fluid balance and returns a non-null response DTO.
     */
    @Test
    void save_success_specialPlan_noDuplicate() {
        FluidBalanceRequest req = buildRequest();
        Patient patient = buildPatient();
        FluidBalance saved = buildFluidBalance(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(subscriptionService.getSubscription(1L)).thenReturn(specialSub());
        when(fluidBalanceRepository.findByDateAndPatientId(any(), eq(1L))).thenReturn(List.of());
        when(fluidBalanceRepository.save(any(FluidBalance.class))).thenReturn(saved);

        FluidBalanceResponse result = fluidBalanceService.save(req);

        assertThat(result).isNotNull();
        verify(fluidBalanceRepository).save(any(FluidBalance.class));
    }

    /**
     * Test that saving a fluid balance when the patient is not found throws a BalanceGlobalException.
     * Verifies that a PATIENT_NOT_FOUND exception is thrown when the patient ID does not exist.
     */
    @Test
    void save_patientNotFound_throwsException() {
        FluidBalanceRequest req = buildRequest();
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fluidBalanceService.save(req))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.PATIENT_NOT_FOUND);
    }

    /**
     * Test that saving a fluid balance with a duplicate date throws a BalanceGlobalException.
     * Verifies that a FLUID_BALANCE_EXIST exception is thrown when a fluid balance
     * already exists for the same date and patient.
     */
    @Test
    void save_duplicateDate_throwsException() {
        FluidBalanceRequest req = buildRequest();
        Patient patient = buildPatient();
        FluidBalance existing = buildFluidBalance(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(subscriptionService.getSubscription(1L)).thenReturn(specialSub());
        when(fluidBalanceRepository.findByDateAndPatientId(any(), eq(1L)))
                .thenReturn(List.of(existing));

        assertThatThrownBy(() -> fluidBalanceService.save(req))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.FLUID_BALANCE_EXIST);
    }

    /**
     * Test that saving a fluid balance when the subscription limit is reached throws a BalanceGlobalException.
     * Verifies that when the current fluid balance count equals the plan's maximum,
     * the service prevents creation and throws an exception.
     */
    @Test
    void save_limitReached_throwsException() {
        FluidBalanceRequest req = buildRequest();
        Patient patient = buildPatient();

        ParametersPlanDto params = new ParametersPlanDto(5, 2, 10, 5, 30);
        PlanDto plan = new PlanDto();
        plan.setName("BASIC");
        plan.setParametersPlan(params);
        SubscriptionDto sub = new SubscriptionDto();
        sub.setPlan(plan);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(subscriptionService.getSubscription(1L)).thenReturn(sub);
        when(fluidBalanceRepository.countByPatientId(1L)).thenReturn(2);

        assertThatThrownBy(() -> fluidBalanceService.save(req))
                .isInstanceOf(BalanceGlobalException.class);
    }

    // ─── updateFluidBalance ───────────────────────────────────────────────────

    /**
     * Test successful update of an existing fluid balance.
     * Verifies that the service finds the fluid balance, updates it, persists the changes,
     * and returns a non-null response DTO.
     */
    @Test
    void updateFluidBalance_success() {
        FluidBalanceRequest req = buildRequest();
        FluidBalance existing = buildFluidBalance(1L);
        FluidBalance updated = buildFluidBalance(1L);

        when(fluidBalanceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(fluidBalanceRepository.save(any(FluidBalance.class))).thenReturn(updated);

        FluidBalanceResponse result = fluidBalanceService.updateFluidBalance(1L, req);

        assertThat(result).isNotNull();
        verify(fluidBalanceRepository).save(any(FluidBalance.class));
    }

    /**
     * Test that updating a non-existent fluid balance throws a BalanceGlobalException.
     * Verifies that a FLUID_BALANCE_DOEST_EXIST exception is thrown when the fluid balance ID
     * does not exist in the repository.
     */
    @Test
    void updateFluidBalance_notFound_throwsException() {
        FluidBalanceRequest req = buildRequest();
        when(fluidBalanceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fluidBalanceService.updateFluidBalance(99L, req))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.FLUID_BALANCE_DOEST_EXIST);
    }

    // ─── deleteFluidBalance ───────────────────────────────────────────────────

    /**
     * Test successful deletion of an existing fluid balance.
     * Verifies that the service calls deleteById on the repository when the record exists.
     */
    @Test
    void deleteFluidBalance_success() {
        FluidBalance fb = buildFluidBalance(1L);
        when(fluidBalanceRepository.findById(1L)).thenReturn(Optional.of(fb));
        doNothing().when(fluidBalanceRepository).deleteById(1L);

        fluidBalanceService.deleteFluidBalance(1L);

        verify(fluidBalanceRepository).deleteById(1L);
    }

    /**
     * Test that deleting a non-existent fluid balance throws a BalanceGlobalException.
     * Verifies that a FLUID_BALANCE_DOEST_EXIST exception is thrown when the ID does not exist.
     */
    @Test
    void deleteFluidBalance_notFound_throwsException() {
        when(fluidBalanceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fluidBalanceService.deleteFluidBalance(99L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.FLUID_BALANCE_DOEST_EXIST);
    }

    // ─── getFluidBalanceByDateAndPatient ──────────────────────────────────────

    /**
     * Test that retrieving fluid balances by date range with an explicit end date returns results.
     * Verifies that the service uses start/end of day boundaries and delegates to the repository,
     * returning a correctly mapped list of response DTOs.
     */
    @Test
    void getFluidBalanceByDateAndPatient_withEndDate_returnsMappedList() {
        Instant start = Instant.parse("2024-06-01T00:00:00Z");
        Instant end = Instant.parse("2024-06-07T23:59:59Z");
        FluidBalance fb = buildFluidBalance(1L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            util.when(() -> Utility.startDay(any())).thenReturn(start);
            util.when(() -> Utility.endDay(any())).thenReturn(end);

            when(fluidBalanceRepository
                    .getFluidBalancesByDateBetweenAndPatientIdOrderByDateAsc(any(), any(), eq(1L)))
                    .thenReturn(List.of(fb));

            List<FluidBalanceResponse> result =
                    fluidBalanceService.getFluidBalanceByDateAndPatient(start, end, 1L);

            assertThat(result).hasSize(1);
        }
    }

    /**
     * Test that retrieving fluid balances with a null end date defaults to the same day.
     * Verifies that when no end date is provided, the service uses the start date's end-of-day
     * and correctly returns an empty list when no records match.
     */
    @Test
    void getFluidBalanceByDateAndPatient_nullEndDate_usesStartDay() {
        Instant start = Instant.parse("2024-06-01T00:00:00Z");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            util.when(() -> Utility.startDay(any())).thenReturn(start);
            util.when(() -> Utility.endDay(any())).thenReturn(start.plusSeconds(86399));

            when(fluidBalanceRepository
                    .getFluidBalancesByDateBetweenAndPatientIdOrderByDateAsc(any(), any(), eq(1L)))
                    .thenReturn(List.of());

            List<FluidBalanceResponse> result =
                    fluidBalanceService.getFluidBalanceByDateAndPatient(start, null, 1L);

            assertThat(result).isEmpty();
        }
    }

    // ─── cleanFluidBalanceForPatientAndUser ───────────────────────────────────

    /**
     * Test that cleanFluidBalanceForPatientAndUser skips processing when no patients are found.
     * Verifies that the subscription service is never called when the user has no patients.
     */
    @Test
    void cleanFluidBalanceForPatientAndUser_noPatientsFound_skips() {
        when(patientRepository.findByUserId(1L)).thenReturn(List.of());

        fluidBalanceService.cleanFluidBalanceForPatientAndUser(1L, Instant.now());

        verifyNoInteractions(subscriptionService);
    }

    /**
     * Test that cleanFluidBalanceForPatientAndUser skips cleanup for users with a special plan.
     * Verifies that the fluid balance repository is never called when the user's subscription
     * is a special plan (unlimited retention).
     */
    @Test
    void cleanFluidBalanceForPatientAndUser_specialPlan_skipsClean() {
        Patient p = buildPatient();
        when(patientRepository.findByUserId(1L)).thenReturn(List.of(p));
        when(subscriptionService.getSubscription(1L)).thenReturn(specialSub());

        fluidBalanceService.cleanFluidBalanceForPatientAndUser(1L, Instant.now());

        verifyNoInteractions(fluidBalanceRepository);
    }

    /**
     * Test that cleanFluidBalanceForPatientAndUser deletes old records for limited plans.
     * Verifies that when the user has a limited plan, the service deletes fluid balances,
     * extra fluids, and vital sign details older than the plan's retention period.
     */
    @Test
    void cleanFluidBalanceForPatientAndUser_limitedPlan_deletesOldRecords() {
        Patient p = buildPatient();
        ParametersPlanDto params = new ParametersPlanDto(5, 50, 10, 5, 30);
        PlanDto plan = new PlanDto();
        plan.setName("BASIC");
        plan.setParametersPlan(params);
        SubscriptionDto sub = new SubscriptionDto();
        sub.setPlan(plan);

        when(patientRepository.findByUserId(1L)).thenReturn(List.of(p));
        when(subscriptionService.getSubscription(1L)).thenReturn(sub);
        doNothing().when(fluidBalanceRepository).deleteByPatientIdAndDateBefore(eq(1L), any());
        doNothing().when(extraFluidRepository).deleteByPatientIdAndDateBefore(eq(1L), any());
        doNothing().when(vitalSignDetailRepository).deleteByPatientIdAndDateBefore(eq(1L), any());

        fluidBalanceService.cleanFluidBalanceForPatientAndUser(1L, Instant.now());

        verify(fluidBalanceRepository).deleteByPatientIdAndDateBefore(eq(1L), any());
        verify(extraFluidRepository).deleteByPatientIdAndDateBefore(eq(1L), any());
        verify(vitalSignDetailRepository).deleteByPatientIdAndDateBefore(eq(1L), any());
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSign;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSignDetail;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.VitalSignDetailRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.VitalSignRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

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
 * Unit tests for the VitalSignService class, covering all public methods and key scenarios, including successful operations and exception handling. The tests use Mockito to mock dependencies and AssertJ for assertions, ensuring that the service behaves correctly under various conditions.
 */
@ExtendWith(MockitoExtension.class)
class VitalSignServiceTest {

    @Mock private VitalSignRepository vitalSignRepository;
    @Mock private VitalSignDetailRepository vitalSignDetailRepository;
    @Mock private SubscriptionService subscriptionService;

    @InjectMocks private VitalSignService vitalSignService;

    /**
     * Helper method to create a SubscriptionDto with a special plan configuration for testing purposes. This method sets up a subscription with parameters that allow for more vital signs than the basic plan, which can be used to test scenarios where the user has a special subscription.
     * @return A SubscriptionDto configured with a special plan that allows for more vital signs, useful for testing scenarios where the user has a special subscription. The parameters are set to allow for 10 vital signs, which is higher than the basic plan limit, enabling tests that require a non-restrictive subscription context.
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
     * Helper method to create a SubscriptionDto with a basic plan configuration that limits the number of vital signs for testing purposes. This method sets up a subscription with parameters that restrict the user to a maximum number of vital signs, which can be used to test scenarios where the user has reached their subscription limit.
     * @param maxVS The maximum number of vital signs allowed by the subscription plan. This parameter allows the test to simulate different limit scenarios, such as when the user has reached their limit or is close to it, enabling tests that verify the service's behavior when the subscription constraints are in effect.
     * @return A SubscriptionDto configured with a basic plan that limits the number of vital signs, useful for testing scenarios where the user has reached their subscription limit. The parameters are set to allow for a specific maximum number of vital signs, which can be used to simulate conditions where the user cannot create more vital signs due to subscription restrictions.
     */
    private SubscriptionDto limitedSub(int maxVS) {
        ParametersPlanDto params = new ParametersPlanDto(5, 50, maxVS, 10, 30);
        PlanDto plan = new PlanDto();
        plan.setName("BASIC");
        plan.setParametersPlan(params);
        SubscriptionDto sub = new SubscriptionDto();
        sub.setPlan(plan);
        return sub;
    }

    /**
     * Helper method to create a VitalSign entity with the specified ID for testing purposes. This method sets up a VitalSign with a name and associated user, which can be used in various tests that require a valid VitalSign entity, such as when testing retrieval, update, or deletion operations.
     * @param id The ID to assign to the created VitalSign entity. This parameter allows the test to create entities with specific IDs, which can be useful for testing scenarios that involve retrieving or manipulating entities by their ID, ensuring that the tests can target specific records in the mocked repository.
     * @return A VitalSign entity with the specified ID, a name of "Blood Pressure", and an associated user with ID 1. This entity can be used in tests that require a valid VitalSign, such as when testing retrieval, update, or deletion operations, providing a consistent and realistic object for the service methods to interact with during testing.
     */
    private VitalSign buildVitalSign(Long id) {
        VitalSign vs = new VitalSign();
        vs.setId(id);
        vs.setName("Blood Pressure");
        User u = new User();
        u.setId(1L);
        vs.setUser(u);
        return vs;
    }

    /**
     * Helper method to create a VitalSignDetail entity with the specified ID for testing purposes. This method sets up a VitalSignDetail with associated patient, vital sign, value, date, and status, which can be used in various tests that require a valid VitalSignDetail entity, such as when testing retrieval, update, or deletion operations.
     * @param id The ID to assign to the created VitalSignDetail entity. This parameter allows the test to create entities with specific IDs, which can be useful for testing scenarios that involve retrieving or manipulating entities by their ID, ensuring that the tests can target specific records in the mocked repository.
     * @return A VitalSignDetail entity with the specified ID, associated with a patient (ID 1), a vital sign (ID 1), a value of "120/80", the current date, and an ACTIVO status. This entity can be used in tests that require a valid VitalSignDetail, such as when testing retrieval, update, or deletion operations, providing a consistent and realistic object for the service methods to interact with during testing.
     */
    private VitalSignDetail buildDetail(Long id) {
        VitalSignDetail vsd = new VitalSignDetail();
        vsd.setId(id);
        vsd.setPatient(new Patient(1L));
        vsd.setVitalSign(buildVitalSign(1L));
        vsd.setValue("120/80");
        vsd.setDate(Instant.now());
        vsd.setStatus(StatusEnum.ACTIVO);
        return vsd;
    }

    /**
     * Helper method to create a VitalSignDetailRequest object for testing purposes. This method sets up a request with a patient ID, current date, associated vital sign, value, and status, which can be used in tests that require a valid request object for creating or updating vital sign details.
     * @return A VitalSignDetailRequest object with a patient ID of 1, the current date, an associated vital sign (ID 1, name "Blood Pressure"), a value of "120/80", and an ACTIVO status. This request can be used in tests that require a valid input for creating or updating vital sign details, providing a consistent and realistic object for the service methods to process during testing.
     */
    private VitalSignDetailRequest buildDetailRequest() {
        VitalSignDetailRequest req = new VitalSignDetailRequest();
        req.setPatientId(1L);
        req.setDate(Instant.now());
        VitalSignResponse vsr = new VitalSignResponse();
        vsr.setId(1L);
        vsr.setName("Blood Pressure");
        req.setVitalSign(vsr);
        req.setValue("120/80");
        req.setStatus(StatusEnum.ACTIVO);
        return req;
    }

    // ─── save ────────────────────────────────────────────────────────────────

    /**
     * Test successful vital sign creation with a special subscription plan.
     * Verifies that when the user has a special plan, the service persists the vital sign
     * and returns a response DTO with the correct name.
     */
    @Test
    void save_specialPlan_success() {
        VitalSignRequest req = new VitalSignRequest(null, "Blood Pressure", 1L);
        VitalSign saved = buildVitalSign(1L);

        when(subscriptionService.getSubscription(1L)).thenReturn(specialSub());
        when(vitalSignRepository.save(any(VitalSign.class))).thenReturn(saved);

        VitalSignResponse result = vitalSignService.save(req);

        assertThat(result.getName()).isEqualTo("Blood Pressure");
        verify(vitalSignRepository).save(any(VitalSign.class));
    }

    /**
     * Test that saving a vital sign when the subscription limit is reached throws a BalanceGlobalException.
     * Verifies that when the current vital sign count equals the plan's maximum,
     * the service prevents creation and throws an exception.
     */
    @Test
    void save_limitReached_throwsException() {
        VitalSignRequest req = new VitalSignRequest(null, "BP", 1L);
        when(subscriptionService.getSubscription(1L)).thenReturn(limitedSub(2));

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserId).thenReturn(1L);
            when(vitalSignRepository.countByUserId(1L)).thenReturn(2);

            assertThatThrownBy(() -> vitalSignService.save(req))
                    .isInstanceOf(BalanceGlobalException.class);
        }
    }

    // ─── getAllVitalSignsByUser ────────────────────────────────────────────────

    /**
     * Test that getAllVitalSignsByUser returns a correctly mapped list of vital sign responses.
     * Verifies that the service retrieves the user ID from the security context and returns
     * all vital signs associated with that user, mapped to response DTOs.
     */
    @Test
    void getAllVitalSignsByUser_returnsMappedList() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserId).thenReturn(1L);
            when(vitalSignRepository.findByUserId(1L)).thenReturn(List.of(buildVitalSign(1L)));

            List<VitalSignResponse> result = vitalSignService.getAllVitalSignsByUser();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Blood Pressure");
        }
    }

    // ─── saveVitalSignDetail ──────────────────────────────────────────────────

    /**
     * Test successful creation of a vital sign detail record.
     * Verifies that the service calculates the start of day, persists the detail entity,
     * and returns a non-null response DTO.
     */
    @Test
    void saveVitalSignDetail_success() {
        VitalSignDetailRequest req = buildDetailRequest();
        VitalSignDetail saved = buildDetail(1L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            util.when(() -> Utility.startDay(any())).thenReturn(Instant.now());

            when(vitalSignDetailRepository.save(any(VitalSignDetail.class))).thenReturn(saved);

            VitalSignDetailResponse result = vitalSignService.saveVitalSignDetail(req);

            assertThat(result).isNotNull();
            verify(vitalSignDetailRepository).save(any(VitalSignDetail.class));
        }
    }

    // ─── updateVitalSignDetail ────────────────────────────────────────────────

    /**
     * Test successful update of an existing vital sign detail record.
     * Verifies that when the detail exists, the service saves the updated entity
     * and returns a non-null response DTO.
     */
    @Test
    void updateVitalSignDetail_success() {
        VitalSignDetailRequest req = buildDetailRequest();
        VitalSignDetail existing = buildDetail(1L);
        VitalSignDetail updated = buildDetail(1L);
        updated.setValue("130/85");

        when(vitalSignDetailRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vitalSignDetailRepository.save(any(VitalSignDetail.class))).thenReturn(updated);

        VitalSignDetailResponse result = vitalSignService.updateVitalSignDetail(req, 1L);

        assertThat(result).isNotNull();
        verify(vitalSignDetailRepository).save(any(VitalSignDetail.class));
    }

    /**
     * Test that updating a non-existent vital sign detail throws a BalanceGlobalException.
     * Verifies that a VITAL_SIGN_DETAIL_NOT_FOUND exception is thrown when the detail ID
     * does not exist in the repository.
     */
    @Test
    void updateVitalSignDetail_notFound_throwsException() {
        VitalSignDetailRequest req = buildDetailRequest();
        when(vitalSignDetailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vitalSignService.updateVitalSignDetail(req, 99L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.VITAL_SIGN_DETAIL_NOT_FOUND);
    }

    // ─── deleteVitalSignDetail ────────────────────────────────────────────────

    /**
     * Test successful deletion of an existing vital sign detail record.
     * Verifies that the service calls deleteById on the repository when the detail exists.
     */
    @Test
    void deleteVitalSignDetail_success() {
        VitalSignDetail detail = buildDetail(1L);
        when(vitalSignDetailRepository.findById(1L)).thenReturn(Optional.of(detail));
        doNothing().when(vitalSignDetailRepository).deleteById(1L);

        vitalSignService.deleteVitalSignDetail(1L);

        verify(vitalSignDetailRepository).deleteById(1L);
    }

    /**
     * Test that deleting a non-existent vital sign detail throws a BalanceGlobalException.
     * Verifies that a VITAL_SIGN_DETAIL_NOT_FOUND exception is thrown when the detail ID
     * does not exist in the repository.
     */
    @Test
    void deleteVitalSignDetail_notFound_throwsException() {
        when(vitalSignDetailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vitalSignService.deleteVitalSignDetail(99L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.VITAL_SIGN_DETAIL_NOT_FOUND);
    }

    // ─── getVitalSignDetailByActualDateAndPatient ─────────────────────────────

    /**
     * Test that retrieving vital sign details by current date and patient returns a mapped list.
     * Verifies that the service calculates start/end of day boundaries, queries the repository
     * with ACTIVO status, and returns the correctly mapped DTOs.
     */
    @Test
    void getVitalSignDetailByActualDateAndPatient_returnsMappedList() {
        Instant now = Instant.now();
        VitalSignDetail d = buildDetail(1L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            util.when(() -> Utility.startDay(any())).thenReturn(now);
            util.when(() -> Utility.endDay(any())).thenReturn(now.plusSeconds(86399));

            when(vitalSignDetailRepository
                    .getVitalSignDetailsByDateIsBetweenAndPatientIdAndStatusOrderByDateAsc(
                            any(), any(), eq(1L), eq(StatusEnum.ACTIVO)))
                    .thenReturn(List.of(d));

            List<VitalSignDetailResponse> result =
                    vitalSignService.getVitalSignDetailByActualDateAndPatient(1L, now);

            assertThat(result).hasSize(1);
        }
    }

    // ─── getVitalSignDetailByDatesAndPatient ──────────────────────────────────

    /**
     * Test that retrieving vital sign details by a date range and patient returns a mapped list.
     * Verifies that the service uses the provided start and end dates to query the repository
     * with ACTIVO status and returns correctly mapped DTOs.
     */
    @Test
    void getVitalSignDetailByDatesAndPatient_returnsMappedList() {
        Instant start = Instant.parse("2024-06-01T00:00:00Z");
        Instant end = Instant.parse("2024-06-07T23:59:59Z");
        VitalSignDetail d = buildDetail(1L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            util.when(() -> Utility.startDay(eq(start))).thenReturn(start);
            util.when(() -> Utility.endDay(eq(end))).thenReturn(end);

            when(vitalSignDetailRepository
                    .getVitalSignDetailsByDateIsBetweenAndPatientIdAndStatusOrderByDateAsc(
                            any(), any(), eq(1L), eq(StatusEnum.ACTIVO)))
                    .thenReturn(List.of(d));

            List<VitalSignDetailResponse> result =
                    vitalSignService.getVitalSignDetailByDatesAndPatient(1L, start, end);

            assertThat(result).hasSize(1);
        }
    }

    // ─── updateVitalSign ──────────────────────────────────────────────────────

    /**
     * Test successful update of an existing vital sign.
     * Verifies that the service finds the vital sign, updates its name, persists it,
     * and returns a response DTO with the updated name.
     */
    @Test
    void updateVitalSign_success() {
        VitalSignRequest req = new VitalSignRequest(1L, "Temperature", 1L);
        VitalSign existing = buildVitalSign(1L);
        VitalSign updated = new VitalSign();
        updated.setId(1L);
        updated.setName("Temperature");

        when(vitalSignRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vitalSignRepository.save(any(VitalSign.class))).thenReturn(updated);

        VitalSignResponse result = vitalSignService.updateVitalSign(1L, req);

        assertThat(result.getName()).isEqualTo("Temperature");
    }

    /**
     * Test that updating a non-existent vital sign throws a BalanceGlobalException.
     * Verifies that a VITAL_SIGN_DETAIL_NOT_FOUND exception is thrown when the vital sign ID
     * does not exist in the repository.
     */
    @Test
    void updateVitalSign_notFound_throwsException() {
        VitalSignRequest req = new VitalSignRequest(99L, "X", 1L);
        when(vitalSignRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vitalSignService.updateVitalSign(99L, req))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.VITAL_SIGN_DETAIL_NOT_FOUND);
    }

    // ─── deleteVitalSign ──────────────────────────────────────────────────────

    /**
     * Test successful deletion of a vital sign and its associated details.
     * Verifies that the service deletes all related vital sign detail records
     * before deleting the vital sign itself.
     */
    @Test
    void deleteVitalSign_success() {
        VitalSign vs = buildVitalSign(1L);
        VitalSignDetail d = buildDetail(1L);
        when(vitalSignRepository.findById(1L)).thenReturn(Optional.of(vs));
        when(vitalSignDetailRepository.findByVitalSignId(1L)).thenReturn(List.of(d));
        doNothing().when(vitalSignDetailRepository).deleteById(d.getId());
        doNothing().when(vitalSignRepository).deleteById(1L);

        vitalSignService.deleteVitalSign(1L);

        verify(vitalSignDetailRepository).deleteById(d.getId());
        verify(vitalSignRepository).deleteById(1L);
    }

    /**
     * Test that deleting a non-existent vital sign throws a BalanceGlobalException.
     * Verifies that a VITAL_SIGN_DETAIL_NOT_FOUND exception is thrown when the vital sign ID
     * does not exist in the repository.
     */
    @Test
    void deleteVitalSign_notFound_throwsException() {
        when(vitalSignRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vitalSignService.deleteVitalSign(99L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.VITAL_SIGN_DETAIL_NOT_FOUND);
    }
}


package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Medicine;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.MedicineDetail;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MedicineDetailRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MedicineRepository;
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
 * Unit tests for MedicineService covering all public methods and key scenarios, including:
 * - Saving medicines with different subscription plans and limits.
 * - Retrieving medicines by user and ensuring correct mapping.
 * - Managing medicine details (create, update, delete) with proper error handling.
 * - Updating and deleting medicines with associated details.
 * Each test verifies both the expected behavior and interactions with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock private MedicineRepository medicineRepository;
    @Mock private MedicineDetailRepository medicineDetailRepository;
    @Mock private SubscriptionService subscriptionService;

    @InjectMocks private MedicineService medicineService;

    /**
     * Helper method to create a SubscriptionDto with a special plan that has no medicine limit.
     * @return A SubscriptionDto configured with a special plan allowing unlimited medicines.
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
     * Helper method to create a SubscriptionDto with a basic plan that has a specified medicine limit.
     * @param maxMeds The maximum number of medicines allowed by the plan.
     * @return A SubscriptionDto configured with a basic plan that has the given medicine limit.
     */
    private SubscriptionDto limitedSub(int maxMeds) {
        ParametersPlanDto params = new ParametersPlanDto(5, 50, 10, maxMeds, 30);
        PlanDto plan = new PlanDto();
        plan.setName("BASIC");
        plan.setParametersPlan(params);
        SubscriptionDto sub = new SubscriptionDto();
        sub.setPlan(plan);
        return sub;
    }

    /**
     * Helper method to build a Medicine entity with the given ID, a fixed name, and an associated user.
     * @param id The ID to assign to the Medicine entity.
     * @return A Medicine entity with the specified ID, name "Aspirin", and a user with ID 1L.
     */
    private Medicine buildMedicine(Long id) {
        Medicine m = new Medicine();
        m.setId(id);
        m.setName("Aspirin");
        User u = new User();
        u.setId(1L);
        m.setUser(u);
        return m;
    }

    /**
     * Helper method to build a MedicineDetail entity with the given ID, associated with a patient and medicine, and with fixed dose, frequency, date, and status.
     * @param id The ID to assign to the MedicineDetail entity.
     * @return A MedicineDetail entity with the specified ID, associated with a patient (ID 1L) and a medicine (ID 1L), dose "200 mg", frequency "12h", current date, and status ACTIVO.
     */
    private MedicineDetail buildDetail(Long id) {
        MedicineDetail md = new MedicineDetail();
        md.setId(id);
        md.setPatient(new Patient(1L));
        md.setMedicine(buildMedicine(1L));
        md.setDose("200 mg");
        md.setFrequency("12h");
        md.setDate(Instant.now());
        md.setStatus(StatusEnum.ACTIVO);
        return md;
    }

    /**
     * Helper method to build a MedicineDetailRequestDto with fixed patient ID, date, medicine, dose, frequency, and status.
     * @return A MedicineDetailRequestDto with patient ID 1L, current date, a medicine response with ID 1L and name "Aspirin", dose "200 mg", frequency "12h", and status ACTIVO.
     */
    private MedicineDetailRequestDto buildDetailRequest() {
        MedicineDetailRequestDto dto = new MedicineDetailRequestDto();
        dto.setPatientId(1L);
        dto.setDate(Instant.now());
        MedicineResponse mr = new MedicineResponse();
        mr.setId(1L);
        mr.setName("Aspirin");
        dto.setMedicine(mr);
        dto.setDose("200 mg");
        dto.setFrequency("12h");
        dto.setStatus(StatusEnum.ACTIVO);
        return dto;
    }

    // ─── save ────────────────────────────────────────────────────────────────

    /**
     * Test successful medicine creation with a special subscription plan.
     * Verifies that when the user has a special plan, the service persists the medicine
     * and returns a response DTO with the correct name.
     */
    @Test
    void save_specialPlan_success() {
        MedicineRequest req = new MedicineRequest(null, "Aspirin", 1L);
        Medicine saved = buildMedicine(1L);

        when(subscriptionService.getSubscription(1L)).thenReturn(specialSub());
        when(medicineRepository.save(any(Medicine.class))).thenReturn(saved);

        MedicineResponse result = medicineService.save(req);

        assertThat(result.getName()).isEqualTo("Aspirin");
        verify(medicineRepository).save(any(Medicine.class));
    }

    /**
     * Test that saving a medicine when the subscription limit is reached throws a BalanceGlobalException.
     * Verifies that when the current medicine count equals the plan's maximum,
     * the service prevents creation and throws an exception.
     */
    @Test
    void save_limitReached_throwsException() {
        MedicineRequest req = new MedicineRequest(null, "Aspirin", 1L);
        when(subscriptionService.getSubscription(1L)).thenReturn(limitedSub(2));

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserId).thenReturn(1L);
            when(medicineRepository.countByUserId(1L)).thenReturn(2);

            assertThatThrownBy(() -> medicineService.save(req))
                    .isInstanceOf(BalanceGlobalException.class);
        }
    }

    // ─── getAllMedicinesByUser ─────────────────────────────────────────────────

    /**
     * Test that getAllMedicinesByUser returns a correctly mapped list of medicine responses.
     * Verifies that the service retrieves the user ID from the security context and returns
     * all medicines associated with that user, mapped to response DTOs.
     */
    @Test
    void getAllMedicinesByUser_returnsMappedList() {
        Medicine m = buildMedicine(1L);
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserId).thenReturn(1L);
            when(medicineRepository.getAllMedicinesByUserId(1L)).thenReturn(List.of(m));

            List<MedicineResponse> result = medicineService.getAllMedicinesByUser();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Aspirin");
        }
    }

    // ─── saveMedicineDetail ───────────────────────────────────────────────────

    /**
     * Test successful creation of a medicine detail record.
     * Verifies that the service persists the detail entity and returns a non-null response DTO.
     */
    @Test
    void saveMedicineDetail_success() {
        MedicineDetailRequestDto req = buildDetailRequest();
        MedicineDetail saved = buildDetail(1L);
        when(medicineDetailRepository.save(any(MedicineDetail.class))).thenReturn(saved);

        MedicineDetailResponseDto result = medicineService.saveMedicineDetail(req);

        assertThat(result).isNotNull();
        verify(medicineDetailRepository).save(any(MedicineDetail.class));
    }

    // ─── updateMedicineDetail ────────────────────────────────────────────────

    /**
     * Test successful update of an existing medicine detail record.
     * Verifies that when the detail exists, the service saves the updated entity
     * and returns a non-null response DTO.
     */
    @Test
    void updateMedicineDetail_success() {
        MedicineDetailRequestDto req = buildDetailRequest();
        req.setStatus(StatusEnum.ACTIVO);
        MedicineDetail existing = buildDetail(1L);
        MedicineDetail updated = buildDetail(1L);
        updated.setDose("400 mg");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);

            when(medicineDetailRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(medicineDetailRepository.save(any(MedicineDetail.class))).thenReturn(updated);

            MedicineDetailResponseDto result = medicineService.updateMedicineDetail(1L, req);

            assertThat(result).isNotNull();
            verify(medicineDetailRepository).save(any(MedicineDetail.class));
        }
    }

    /**
     * Test that updating a non-existent medicine detail throws a BalanceGlobalException.
     * Verifies that a MEDICINE_DETAIL_NOT_FOUND exception is thrown when the detail ID
     * does not exist in the repository.
     */
    @Test
    void updateMedicineDetail_notFound_throwsException() {
        MedicineDetailRequestDto req = buildDetailRequest();
        when(medicineDetailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.updateMedicineDetail(99L, req))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.MEDICINE_DETAIL_NOT_FOUND);
    }

    // ─── deleteMedicineDetail ─────────────────────────────────────────────────

    /**
     * Test successful deletion of an existing medicine detail record.
     * Verifies that the service calls deleteById on the repository when the detail exists.
     */
    @Test
    void deleteMedicineDetail_success() {
        MedicineDetail detail = buildDetail(1L);
        when(medicineDetailRepository.findById(1L)).thenReturn(Optional.of(detail));
        doNothing().when(medicineDetailRepository).deleteById(1L);

        medicineService.deleteMedicineDetail(1L);

        verify(medicineDetailRepository).deleteById(1L);
    }

    /**
     * Test that deleting a non-existent medicine detail throws a BalanceGlobalException.
     * Verifies that a MEDICINE_DETAIL_NOT_FOUND exception is thrown when the detail ID
     * does not exist in the repository.
     */
    @Test
    void deleteMedicineDetail_notFound_throwsException() {
        when(medicineDetailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.deleteMedicineDetail(99L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.MEDICINE_DETAIL_NOT_FOUND);
    }

    // ─── getVitalSignDetailByPatient (medicine details by patient) ────────────

    /**
     * Test that retrieving medicine details by patient returns a correctly mapped list.
     * Verifies that the service queries the repository with ACTIVO status filter
     * and returns medicine detail DTOs for the given patient.
     */
    @Test
    void getVitalSignDetailByPatient_returnsMappedList() {
        MedicineDetail detail = buildDetail(1L);
        when(medicineDetailRepository.getMedicineDetailByPatientIdAndStatusOrderByDateAsc(1L, StatusEnum.ACTIVO))
                .thenReturn(List.of(detail));

        List<MedicineDetailResponseDto> result = medicineService.getVitalSignDetailByPatient(1L);

        assertThat(result).hasSize(1);
    }

    // ─── updateMedicine ───────────────────────────────────────────────────────

    /**
     * Test successful update of an existing medicine.
     * Verifies that the service finds the medicine, updates its name, persists it,
     * and returns a response DTO with the updated name.
     */
    @Test
    void updateMedicine_success() {
        MedicineRequest req = new MedicineRequest(1L, "Ibuprofen", 1L);
        Medicine existing = buildMedicine(1L);
        Medicine updated = new Medicine();
        updated.setId(1L);
        updated.setName("Ibuprofen");

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(updated);

        MedicineResponse result = medicineService.updateMedicine(1L, req);

        assertThat(result.getName()).isEqualTo("Ibuprofen");
    }

    /**
     * Test that updating a non-existent medicine throws a BalanceGlobalException.
     * Verifies that a MEDICINE_NOT_FOUND exception is thrown when the medicine ID
     * does not exist in the repository.
     */
    @Test
    void updateMedicine_notFound_throwsException() {
        MedicineRequest req = new MedicineRequest(99L, "X", 1L);
        when(medicineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.updateMedicine(99L, req))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.MEDICINE_NOT_FOUND);
    }

    // ─── deleteMedicine ───────────────────────────────────────────────────────

    /**
     * Test successful deletion of a medicine and its associated details.
     * Verifies that the service deletes all related medicine detail records
     * before deleting the medicine itself.
     */
    @Test
    void deleteMedicine_success() {
        Medicine m = buildMedicine(1L);
        MedicineDetail d = buildDetail(1L);
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(m));
        when(medicineDetailRepository.findByMedicineId(1L)).thenReturn(List.of(d));
        doNothing().when(medicineDetailRepository).deleteById(d.getId());
        doNothing().when(medicineRepository).deleteById(1L);

        medicineService.deleteMedicine(1L);

        verify(medicineDetailRepository).deleteById(d.getId());
        verify(medicineRepository).deleteById(1L);
    }

    /**
     * Test that deleting a non-existent medicine throws a BalanceGlobalException.
     * Verifies that a MEDICINE_NOT_FOUND exception is thrown when the medicine ID
     * does not exist in the repository.
     */
    @Test
    void deleteMedicine_notFound_throwsException() {
        when(medicineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.deleteMedicine(99L))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.MEDICINE_NOT_FOUND);
    }
}


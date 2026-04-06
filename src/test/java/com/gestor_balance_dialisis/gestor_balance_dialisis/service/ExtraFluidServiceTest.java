package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ExtraFluidRequestDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ExtraFluidResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.ExtraFluid;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.ExtraFluidRepository;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExtraFluidService.
 *
 * This test class verifies the behavior of the ExtraFluidService methods, including:
 * - Saving a new extra fluid record and returning the correct DTO.
 * - Retrieving extra fluids by date and patient with proper date boundary handling.
 * - Updating an existing extra fluid record and handling not found cases.
 * - Deleting an extra fluid record and handling not found cases.
 *
 * The tests use Mockito to mock dependencies and AssertJ for assertions.
 */
@ExtendWith(MockitoExtension.class)
class ExtraFluidServiceTest {

    @Mock private ExtraFluidRepository extraFluidRepository;

    @InjectMocks private ExtraFluidService extraFluidService;

    /**
     * Helper method to build an ExtraFluid entity with preset values for testing.
     * @param id The ID to assign to the ExtraFluid entity.
     * @return An ExtraFluid instance with the specified ID and preset patient, urine, ingested, and date values.
     */
    private ExtraFluid buildExtraFluid(Long id) {
        ExtraFluid ef = new ExtraFluid();
        ef.setId(id);
        Patient p = new Patient();
        p.setId(1L);
        ef.setPatient(p);
        ef.setUrine(new BigDecimal("300.00"));
        ef.setIngested(new BigDecimal("150.00"));
        ef.setDate(Instant.parse("2024-06-01T12:00:00Z"));
        return ef;
    }

    /**
     * Helper method to build an ExtraFluidRequestDto with preset values for testing.
     * @param id The ID to assign to the ExtraFluidRequestDto.
     * @return An ExtraFluidRequestDto instance with the specified ID and preset patientId, urine, ingested, and date values.
     */
    private ExtraFluidRequestDto buildRequest(Long id) {
        ExtraFluidRequestDto req = new ExtraFluidRequestDto();
        req.setId(id);
        req.setPatientId(1L);
        req.setUrine(new BigDecimal("300.00"));
        req.setIngested(new BigDecimal("150.00"));
        req.setDate(Instant.parse("2024-06-01T12:00:00Z"));
        return req;
    }

    // ─── save ────────────────────────────────────────────────────────────────

    /**
     * Test that saving an extra fluid record persists the entity and returns a correctly mapped DTO.
     * Verifies that the returned response contains the expected patient ID and urine value,
     * and that the repository save method is invoked once.
     */
    @Test
    void save_persistsAndReturnsDto() {
        ExtraFluidRequestDto req = buildRequest(null);
        ExtraFluid saved = buildExtraFluid(1L);
        when(extraFluidRepository.save(any(ExtraFluid.class))).thenReturn(saved);

        ExtraFluidResponseDto result = extraFluidService.save(req);

        assertThat(result.getPatientId()).isEqualTo(1L);
        assertThat(result.getUrine()).isEqualByComparingTo("300.00");
        verify(extraFluidRepository).save(any(ExtraFluid.class));
    }

    // ─── getExtraFluidByActualDateAndPatient ──────────────────────────────────

    /**
     * Test that retrieving extra fluids by the current date and patient returns a correctly mapped list.
     * Verifies that the service calculates start/end of day boundaries using Utility helpers
     * and maps the repository results to DTOs with the correct patient ID.
     */
    @Test
    void getExtraFluidByActualDateAndPatient_returnsMappedList() {
        Instant now = Instant.now();
        ExtraFluid ef = buildExtraFluid(1L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {

            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            util.when(() -> Utility.startDay(any())).thenReturn(now);
            util.when(() -> Utility.endDay(any())).thenReturn(now.plusSeconds(86399));

            when(extraFluidRepository.getExtraFluidByDateIsBetweenAndPatientId(any(), any(), eq(1L)))
                    .thenReturn(List.of(ef));

            List<ExtraFluidResponseDto> result = extraFluidService.getExtraFluidByActualDateAndPatient(1L, now);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPatientId()).isEqualTo(1L);
        }
    }

    // ─── getExtraFluidByDateAndPatient ────────────────────────────────────────

    /**
     * Test that retrieving extra fluids by a date range with an explicit end date returns results.
     * Verifies that the service delegates to the repository with the correct start and end boundaries
     * and returns a non-empty mapped list.
     */
    @Test
    void getExtraFluidByDateAndPatient_withEndDate_returnsMappedList() {
        Instant start = Instant.parse("2024-06-01T00:00:00Z");
        Instant end = Instant.parse("2024-06-07T00:00:00Z");
        ExtraFluid ef = buildExtraFluid(1L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {

            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            util.when(() -> Utility.startDay(any())).thenReturn(start);
            util.when(() -> Utility.endDay(any())).thenReturn(end);

            when(extraFluidRepository.getExtraFluidByDateIsBetweenAndPatientId(any(), any(), eq(1L)))
                    .thenReturn(List.of(ef));

            List<ExtraFluidResponseDto> result =
                    extraFluidService.getExtraFluidByDateAndPatient(1L, start, end);

            assertThat(result).hasSize(1);
        }
    }

    /**
     * Test that retrieving extra fluids by date range with a null end date defaults to the same day.
     * Verifies that when no end date is provided, the service uses the start date's end-of-day
     * as the upper boundary and correctly returns an empty list when no records match.
     */
    @Test
    void getExtraFluidByDateAndPatient_nullEndDate_usesStartDay() {
        Instant start = Instant.parse("2024-06-01T00:00:00Z");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {

            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            util.when(() -> Utility.startDay(any())).thenReturn(start);
            util.when(() -> Utility.endDay(any())).thenReturn(start.plusSeconds(86399));

            when(extraFluidRepository.getExtraFluidByDateIsBetweenAndPatientId(any(), any(), eq(1L)))
                    .thenReturn(List.of());

            List<ExtraFluidResponseDto> result =
                    extraFluidService.getExtraFluidByDateAndPatient(1L, start, null);

            assertThat(result).isEmpty();
        }
    }

    // ─── updateExtraFluid ─────────────────────────────────────────────────────

    /**
     * Test successful update of an existing extra fluid record.
     * Verifies that when the record exists, the service saves the updated entity
     * and returns a non-null response DTO.
     */
    @Test
    void updateExtraFluid_success() {
        ExtraFluidRequestDto req = buildRequest(1L);
        ExtraFluid existing = buildExtraFluid(1L);
        ExtraFluid updated = buildExtraFluid(1L);
        updated.setIngested(new BigDecimal("200.00"));

        when(extraFluidRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(extraFluidRepository.save(any(ExtraFluid.class))).thenReturn(updated);

        ExtraFluidResponseDto result = extraFluidService.updateExtraFluid(1L, req);

        assertThat(result).isNotNull();
        verify(extraFluidRepository).save(any(ExtraFluid.class));
    }

    /**
     * Test that updating a non-existent extra fluid record throws a BalanceGlobalException.
     * Verifies that when the record is not found in the repository, the service throws
     * an exception indicating the extra fluid was not found.
     */
    @Test
    void updateExtraFluid_notFound_throwsException() {
        ExtraFluidRequestDto req = buildRequest(99L);
        when(extraFluidRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> extraFluidService.updateExtraFluid(99L, req))
                .isInstanceOf(BalanceGlobalException.class);
    }

    // ─── deleteExtraFluid ─────────────────────────────────────────────────────

    /**
     * Test successful deletion of an existing extra fluid record.
     * Verifies that when the record exists, the service calls deleteById on the repository.
     */
    @Test
    void deleteExtraFluid_success() {
        ExtraFluid ef = buildExtraFluid(1L);
        when(extraFluidRepository.findById(1L)).thenReturn(Optional.of(ef));
        doNothing().when(extraFluidRepository).deleteById(1L);

        extraFluidService.deleteExtraFluid(1L);

        verify(extraFluidRepository).deleteById(1L);
    }

    /**
     * Test that deleting a non-existent extra fluid record throws a BalanceGlobalException.
     * Verifies that when the record is not found in the repository, the service throws
     * an exception instead of attempting deletion.
     */
    @Test
    void deleteExtraFluid_notFound_throwsException() {
        when(extraFluidRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> extraFluidService.deleteExtraFluid(99L))
                .isInstanceOf(BalanceGlobalException.class);
    }
}


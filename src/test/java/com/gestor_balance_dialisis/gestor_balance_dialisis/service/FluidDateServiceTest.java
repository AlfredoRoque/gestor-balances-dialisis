package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidDate;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.FluidDateRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test class for FluidDateService.
 * This class contains unit tests for the getAllFluidDates method of FluidDateService.
 * It verifies that the service correctly retrieves active fluid dates from the repository,
 * formats them as time strings, and handles edge cases such as empty results.
 * The tests also ensure that the service interacts with the repository using the correct status filter.
 */
@ExtendWith(MockitoExtension.class)
class FluidDateServiceTest {

    @Mock private FluidDateRepository fluidDateRepository;

    @InjectMocks private FluidDateService fluidDateService;

    /**
     * Helper method to build a FluidDate entity with a specific time.
     * @param time A string representing the time in HH:mm format (e.g., "08:00").
     * @return A FluidDate entity with the date set to today at the specified time in UTC and status ACTIVO.
     */
    private FluidDate buildFluidDate(String time) {
        FluidDate fd = new FluidDate();
        fd.setId(1L);
        // e.g. "08:00" → parse as today at 08:00 UTC
        fd.setDate(Instant.parse("2024-06-01T" + time + ":00Z"));
        fd.setStatus(StatusEnum.ACTIVO);
        return fd;
    }

    /**
     * Test that getAllFluidDates returns a list of formatted time strings.
     * Verifies that the service retrieves active fluid dates from the repository
     * and formats them as HH:mm time strings in the user's time zone.
     */
    @Test
    void getAllFluidDates_returnsFormattedTimeList() {
        FluidDate fd1 = buildFluidDate("08:00");
        FluidDate fd2 = buildFluidDate("14:00");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            when(fluidDateRepository.getFluidDateByStatus(StatusEnum.ACTIVO)).thenReturn(List.of(fd1, fd2));

            List<String> result = fluidDateService.getAllFluidDates();

            assertThat(result).hasSize(2);
            assertThat(result).contains("08:00", "14:00");
        }
    }

    /**
     * Test that getAllFluidDates returns an empty list when no active fluid dates exist.
     * Verifies that the service correctly handles the empty result from the repository.
     */
    @Test
    void getAllFluidDates_empty_returnsEmptyList() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            when(fluidDateRepository.getFluidDateByStatus(StatusEnum.ACTIVO)).thenReturn(List.of());

            List<String> result = fluidDateService.getAllFluidDates();

            assertThat(result).isEmpty();
        }
    }

    /**
     * Test that getAllFluidDates filters by ACTIVO status.
     * Verifies that the repository is queried specifically with StatusEnum.ACTIVO,
     * ensuring only active fluid dates are retrieved.
     */
    @Test
    void getAllFluidDates_usesActiveStatus() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);
            when(fluidDateRepository.getFluidDateByStatus(StatusEnum.ACTIVO)).thenReturn(List.of());

            fluidDateService.getAllFluidDates();

            verify(fluidDateRepository).getFluidDateByStatus(StatusEnum.ACTIVO);
        }
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.RowDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.VitalSignDetailResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.VitalSignDialysisReportDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.VitalSignResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

/**
 * Unit tests for the ReportService class, specifically testing the buildTable method which transforms a list of VitalSignDetailResponse objects into a VitalSignDialysisReportDto containing columns and rows for reporting purposes.
 * The tests cover various scenarios including handling of empty input, extraction of unique and sorted columns, grouping of rows by date, sorting of rows by date, formatting of the day field, and handling of duplicate vital sign entries for the same date. Mocking is used to control the behavior of SecurityUtils for time zone retrieval during testing.
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks private ReportService reportService;

    // ─── buildTable ────────────────────────────────────────────────────────────

    /**
     * Test that buildTable returns empty columns and rows when given an empty list.
     * Verifies that the method handles an empty input gracefully without throwing exceptions.
     */
    @Test
    void buildTable_emptyList_returnsEmptyColumnsAndRows() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneId.of("UTC"));

            VitalSignDialysisReportDto result = reportService.buildTable(Collections.emptyList());

            assertThat(result.getColumns()).isEmpty();
            assertThat(result.getRows()).isEmpty();
        }
    }

    /**
     * Test that buildTable correctly extracts unique and sorted column names.
     * Verifies that duplicate vital sign names are deduplicated and the columns are sorted alphabetically.
     */
    @Test
    void buildTable_extractsUniqueAndSortedColumns() {
        VitalSignResponse bp = new VitalSignResponse(1L, "Blood Pressure");
        VitalSignResponse hr = new VitalSignResponse(2L, "Heart Rate");

        Instant now = Instant.parse("2024-06-01T10:00:00Z");

        VitalSignDetailResponse v1 = new VitalSignDetailResponse();
        v1.setDate(now);
        v1.setVitalSign(bp);
        v1.setValue("120/80");

        VitalSignDetailResponse v2 = new VitalSignDetailResponse();
        v2.setDate(now);
        v2.setVitalSign(hr);
        v2.setValue("72");

        VitalSignDetailResponse v3 = new VitalSignDetailResponse();
        v3.setDate(Instant.parse("2024-06-02T10:00:00Z"));
        v3.setVitalSign(bp);
        v3.setValue("130/85");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneId.of("UTC"));

            VitalSignDialysisReportDto result = reportService.buildTable(List.of(v1, v2, v3));

            assertThat(result.getColumns()).containsExactly("Blood Pressure", "Heart Rate");
        }
    }

    /**
     * Test that buildTable groups vital sign details by date and creates rows.
     * Verifies that entries with the same date are grouped into a single row,
     * with each vital sign name mapped to its value.
     */
    @Test
    void buildTable_groupsByDate_createsRows() {
        VitalSignResponse bp = new VitalSignResponse(1L, "Blood Pressure");
        VitalSignResponse hr = new VitalSignResponse(2L, "Heart Rate");

        Instant date1 = Instant.parse("2024-06-01T10:00:00Z");
        Instant date2 = Instant.parse("2024-06-02T10:00:00Z");

        VitalSignDetailResponse v1 = new VitalSignDetailResponse();
        v1.setDate(date1);
        v1.setVitalSign(bp);
        v1.setValue("120/80");

        VitalSignDetailResponse v2 = new VitalSignDetailResponse();
        v2.setDate(date1);
        v2.setVitalSign(hr);
        v2.setValue("72");

        VitalSignDetailResponse v3 = new VitalSignDetailResponse();
        v3.setDate(date2);
        v3.setVitalSign(bp);
        v3.setValue("130/85");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneId.of("UTC"));

            VitalSignDialysisReportDto result = reportService.buildTable(List.of(v1, v2, v3));

            assertThat(result.getRows()).hasSize(2);

            RowDto firstRow = result.getRows().get(0);
            assertThat(firstRow.getDate()).isEqualTo(date1);
            assertThat(firstRow.getValues()).containsEntry("Blood Pressure", "120/80");
            assertThat(firstRow.getValues()).containsEntry("Heart Rate", "72");

            RowDto secondRow = result.getRows().get(1);
            assertThat(secondRow.getDate()).isEqualTo(date2);
            assertThat(secondRow.getValues()).containsEntry("Blood Pressure", "130/85");
        }
    }

    /**
     * Test that buildTable sorts rows by date in ascending order.
     * Verifies that when entries are provided in non-chronological order,
     * the resulting rows are sorted from earliest to latest date.
     */
    @Test
    void buildTable_sortsByDateAscending() {
        VitalSignResponse bp = new VitalSignResponse(1L, "Blood Pressure");

        Instant earlier = Instant.parse("2024-06-01T10:00:00Z");
        Instant later = Instant.parse("2024-06-05T10:00:00Z");

        VitalSignDetailResponse v1 = new VitalSignDetailResponse();
        v1.setDate(later);
        v1.setVitalSign(bp);
        v1.setValue("130/85");

        VitalSignDetailResponse v2 = new VitalSignDetailResponse();
        v2.setDate(earlier);
        v2.setVitalSign(bp);
        v2.setValue("120/80");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneId.of("UTC"));

            VitalSignDialysisReportDto result = reportService.buildTable(List.of(v1, v2));

            assertThat(result.getRows()).hasSize(2);
            assertThat(result.getRows().get(0).getDate()).isEqualTo(earlier);
            assertThat(result.getRows().get(1).getDate()).isEqualTo(later);
        }
    }

    /**
     * Test that buildTable sets the formatted day on each row.
     * Verifies that the day field is populated using the dd/MM/yyyy format
     * based on the user's time zone.
     */
    @Test
    void buildTable_setsFormattedDay() {
        VitalSignResponse bp = new VitalSignResponse(1L, "Blood Pressure");

        Instant date = Instant.parse("2024-06-15T14:00:00Z");

        VitalSignDetailResponse v1 = new VitalSignDetailResponse();
        v1.setDate(date);
        v1.setVitalSign(bp);
        v1.setValue("120/80");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneId.of("UTC"));

            VitalSignDialysisReportDto result = reportService.buildTable(List.of(v1));

            assertThat(result.getRows()).hasSize(1);
            assertThat(result.getRows().get(0).getDay()).isEqualTo("15/06/2024");
        }
    }

    /**
     * Test that buildTable handles duplicate vital sign entries for the same date.
     * Verifies that when the same vital sign appears multiple times for the same date,
     * only the first value is kept (deduplication behavior).
     */
    @Test
    void buildTable_duplicateVitalSignSameDate_keepsFirst() {
        VitalSignResponse bp = new VitalSignResponse(1L, "Blood Pressure");

        Instant date = Instant.parse("2024-06-01T10:00:00Z");

        VitalSignDetailResponse v1 = new VitalSignDetailResponse();
        v1.setDate(date);
        v1.setVitalSign(bp);
        v1.setValue("120/80");

        VitalSignDetailResponse v2 = new VitalSignDetailResponse();
        v2.setDate(date);
        v2.setVitalSign(bp);
        v2.setValue("999/999");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneId.of("UTC"));

            VitalSignDialysisReportDto result = reportService.buildTable(List.of(v1, v2));

            assertThat(result.getRows()).hasSize(1);
            assertThat(result.getRows().get(0).getValues().get("Blood Pressure")).isEqualTo("120/80");
        }
    }

    /**
     * Test that buildTable correctly applies a non-UTC time zone when formatting the day.
     * Verifies that a date at midnight UTC is formatted as the previous day
     * when the user's time zone is behind UTC (e.g., America/New_York).
     */
    @Test
    void buildTable_nonUtcTimeZone_formatsCorrectly() {
        VitalSignResponse bp = new VitalSignResponse(1L, "Blood Pressure");

        // Midnight UTC → still June 14 in New York (UTC-4 in summer)
        Instant date = Instant.parse("2024-06-15T02:00:00Z");

        VitalSignDetailResponse v1 = new VitalSignDetailResponse();
        v1.setDate(date);
        v1.setVitalSign(bp);
        v1.setValue("120/80");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneId.of("America/New_York"));

            VitalSignDialysisReportDto result = reportService.buildTable(List.of(v1));

            assertThat(result.getRows()).hasSize(1);
            assertThat(result.getRows().get(0).getDay()).isEqualTo("14/06/2024");
        }
    }
}


package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for generating reports based on fluid balance data, including details about fluid balances, medicines, and vital signs.
 * It utilizes JasperReports to create PDF reports from the provided data.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {

    private final VitalSignService vitalSignService;

    /**
     * Generates a PDF report based on the provided list of CalculateFluidBalanceResponseDto objects.
     *
     * @param fluidBalanceResponse the list of CalculateFluidBalanceResponseDto objects containing the data for the report
     * @return a byte array representing the generated PDF report
     * @throws Exception if an error occurs during report generation
     */
    public byte[] generateReport(List<CalculateFluidBalanceResponseDto> fluidBalanceResponse) throws Exception {
        log.info(" balances size : {}",fluidBalanceResponse.size());
        List<VitalSignResponse> allVitalSigns = vitalSignService.getAllVitalSignsByUser();
        InputStream mainStream = new ClassPathResource("/reports/Dialisis_main.jasper").getInputStream();

        Map<String, Object> params = new HashMap<>();
        params.put("SUBREPORT_DIR",
                new ClassPathResource("/reports/").getURL().toString()
        );

        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ZoneId zone = SecurityUtils.getUserZone();
        fluidBalanceResponse.forEach(fluidBalance -> fluidBalance.getFluidBalances().forEach(fluid -> {
            fluid.setHour(fluid.getDate().atZone(zone)
                    .format(formatterHour));
            fluid.setDay(fluid.getDate().atZone(zone)
                    .format(formatterDay));
        }));

        fluidBalanceResponse.forEach(medicines -> medicines.getMedicineDetails().forEach(medicine -> {
            medicine.setDay(medicine.getDate().atZone(zone)
                    .format(formatterDay));
        }));

        fluidBalanceResponse.forEach(vitalSigns -> vitalSigns.getVitalSignDetails().forEach(vitalSign -> {
            vitalSign.setDay(vitalSign.getDate().atZone(zone)
                    .format(formatterDay));
        }));

        params.put("BALANCES", new JRBeanCollectionDataSource(fluidBalanceResponse.stream()
                .flatMap(response -> response.getFluidBalances().stream())
                .toList()));

        List<VitalSignDetailResponse> vitalSignFiltered = fluidBalanceResponse.stream()
                .flatMap(response -> response.getVitalSignDetails().stream())
                .collect(Collectors.toMap(v -> v.getDate() + "-" + v.getVitalSign().getId(), // llave unica
                        v -> v, (v1, v2) -> v1 // si hay repetido se queda con el primero
                )).values().stream().sorted(Comparator.comparing(VitalSignDetailResponse::getDate)).toList();


        VitalSignDialysisReportDto reportDto = buildTable(vitalSignFiltered);
        params.put("VITAL_SIGNS",
                new JRBeanCollectionDataSource(reportDto.getRows()));
        params.put("COLUMNS", reportDto.getColumns());

        params.put("MEDICINES", new JRBeanCollectionDataSource(fluidBalanceResponse.stream()
                .flatMap(response -> response.getMedicineDetails().stream())
                .collect(Collectors.toMap(
                        MedicineDetailResponseDto::getId,
                        med -> med, (a, b) -> a
                )).values().stream().toList()));

        JasperPrint print = JasperFillManager.fillReport(mainStream, params, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf(print);
    }

    /**
     * Builds a VitalSignDialysisReportDto object based on the provided list of VitalSignDetailResponse objects.
     *
     * @param vitalSigns the list of VitalSignDetailResponse objects containing the vital sign details to be included in the report
     * @return a VitalSignDialysisReportDto object containing the columns and rows for the report
     */
    public VitalSignDialysisReportDto buildTable(List<VitalSignDetailResponse> vitalSigns) {
        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Get unique and sorted column names
        List<String> columns = vitalSigns.stream()
                .map(v -> v.getVitalSign().getName())
                .distinct()
                .sorted()
                .toList();
        // group by date and create a map of vital sign name to value
        Map<Instant, Map<String, String>> groupedByDate =
                vitalSigns.stream()
                        .collect(Collectors.groupingBy(
                                VitalSignDetailResponse::getDate,
                                Collectors.toMap(
                                        v -> v.getVitalSign().getName(),
                                        VitalSignDetailResponse::getValue,
                                        (v1, v2) -> v1 // si hay duplicados
                                )
                        ));

        // create a list of RowDto objects sorted by date
        List<RowDto> rows = groupedByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new RowDto(entry.getKey(), entry.getValue()))
                .toList();

        rows.forEach(rowDto -> {
            String day = rowDto.getDate().atZone(SecurityUtils.getUserZone()).format(formatterDay);
            rowDto.setDay(day);
        });
        return new VitalSignDialysisReportDto(columns, rows);
    }
}

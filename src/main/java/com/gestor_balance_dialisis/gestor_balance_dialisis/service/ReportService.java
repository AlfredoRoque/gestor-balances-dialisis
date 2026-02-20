package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for generating reports based on fluid balance data, including details about fluid balances, medicines, and vital signs.
 * It utilizes JasperReports to create PDF reports from the provided data.
 */
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

        List<VitalSignResponse> allVitalSigns = vitalSignService.getAllVitalSigns();
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

        Map<String, VitalSignReportDto> map = new LinkedHashMap<>();

        List<VitalSignDetailResponse> vitalSignFiltered = fluidBalanceResponse.stream()
                .flatMap(response -> response.getVitalSignDetails().stream())
                .collect(Collectors.toMap(v -> v.getDate() + "-" + v.getVitalSign().getId(), // llave unica
                        v -> v, (v1, v2) -> v1 // si hay repetido se queda con el primero
                )).values().stream().sorted(Comparator.comparing(VitalSignDetailResponse::getDate)).toList();

        VitalSignResponse vitalSignPressure = allVitalSigns.stream().
                filter(v -> Objects.equals(v.getName(), "Presión Arterial")).findFirst().orElseThrow(() -> new BalanceGlobalException("Vital sign not found", HttpStatus.CONFLICT.value()));
        VitalSignResponse vitalSignGlucose = allVitalSigns.stream().
                filter(v -> Objects.equals(v.getName(), "Glucosa")).findFirst().orElseThrow(() -> new BalanceGlobalException("Vital sign not found", HttpStatus.CONFLICT.value()));
        for (VitalSignDetailResponse v : vitalSignFiltered) {
            String day = v.getDate().atZone(zone).format(formatterDay);
            map.putIfAbsent(day, new VitalSignReportDto(day, null, null));
            VitalSignReportDto dto = map.get(day);
            Long vitalSignId = v.getVitalSign().getId();
            if (Objects.equals(vitalSignId, vitalSignPressure.getId())) { // bloodPressure
                dto.setBloodPressure(v.getValue());
            }
            if (Objects.equals(vitalSignId, vitalSignGlucose.getId())) { // glucose
                dto.setGlucose(v.getValue());
            }
        }
        List<VitalSignReportDto> reportList = new ArrayList<>(map.values());

        params.put("VITAL_SIGNS", new JRBeanCollectionDataSource(reportList));
        params.put("MEDICINES", new JRBeanCollectionDataSource(fluidBalanceResponse.stream()
                .flatMap(response -> response.getMedicineDetails().stream())
                .collect(Collectors.toMap(
                        MedicineDetailResponseDto::getId,
                        med -> med, (a, b) -> a
                )).values().stream().toList()));

        JasperPrint print = JasperFillManager.fillReport(mainStream, params, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf(print);
    }
}

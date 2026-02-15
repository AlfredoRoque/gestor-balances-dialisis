package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidBalance;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for managing fluid balance records, including saving new records and retrieving existing ones.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FluidBalanceService {

    private final FluidBalanceRepository fluidBalanceRepository;
    private final ExtraFluidRepository extraFluidRepository;
    private final VitalSignDetailRepository vitalSignDetailRepository;
    private final MedicineDetailRepository medicineDetailRepository;
    private final ReportService reportService;
    private final PatientRepository patientRepository;
    private final MailService mailService;

    /**
     * Saves a new fluid balance record based on the provided request data.
     *
     * @param fluidBalanceRequest the fluid balance request containing the record's information
     * @return FluidBalanceResponse containing the saved fluid balance record
     */
    public FluidBalanceResponse save(FluidBalanceRequest fluidBalanceRequest) {
        return new FluidBalanceResponse(fluidBalanceRepository.save(new FluidBalance(fluidBalanceRequest)));
    }

    /**
     * Retrieves fluid balance records based on the provided date range.
     *
     * @param startLocalDate the start date for filtering fluid balance records
     * @param endLocalDate   the end date for filtering fluid balance records (optional)
     * @param patientId      the ID of the patient whose fluid balance records are to be retrieved
     * @return List of FluidBalanceResponse containing the filtered fluid balance records
     */
    public List<FluidBalanceResponse> getFluidBalanceByDateAndPatient(LocalDateTime startLocalDate, LocalDateTime endLocalDate,
                                                            Long patientId) {
        LocalDateTime startDate = Utility.startDay(startLocalDate);
        LocalDateTime endDate = Objects.nonNull(endLocalDate)?endLocalDate:Utility.endDay(startDate);

        return fluidBalanceRepository.getFluidBalancesByDateBetweenAndPatientIdOrderByDateAsc(startDate, endDate,patientId).stream().map(FluidBalanceResponse::new).toList();
    }

    /**
     * Calculates the fluid balance for a patient based on the provided date range.
     * If both startDate and endDate are null, it calculates the balance for the current day.
     *
     * @param patientId the ID of the patient whose fluid balance is to be calculated
     * @param startDate the start date for filtering records (optional)
     * @param endDate   the end date for filtering records (optional)
     * @return List of CalculateFluidBalanceResponseDto containing the calculated fluid balance information
     */
    public List<CalculateFluidBalanceResponseDto> calculateBalanceFluidForPatient(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
        List<CalculateFluidBalanceResponseDto> responseDtoList = new ArrayList<>();
        if (Objects.isNull(startDate)&&Objects.isNull(endDate)) {
            LocalDateTime actualDate = LocalDateTime.now();
            LocalDateTime actualStartDate = Utility.startDay(actualDate);
            LocalDateTime actualEndDate = Utility.endDay(actualStartDate);
            // Process to calculate fluid balance by actual day
            responseDtoList.add(this.getBalancesInformation(actualDate, patientId, actualStartDate, actualEndDate));
        }else {
            // Process to calculate fluid balance by startDate and endDate range
            for (LocalDateTime dt = startDate; !dt.isAfter(endDate); dt = dt.plusDays(1)) {
                LocalDateTime actualStartDate = Utility.startDay(dt);
                LocalDateTime actualEndDate = Utility.endDay(actualStartDate);
                CalculateFluidBalanceResponseDto balanceInformation = this.getBalancesInformation(dt, patientId, actualStartDate, actualEndDate);
                if (Objects.nonNull(balanceInformation)) {
                    responseDtoList.add(balanceInformation);
                }
            }
        }
        return responseDtoList;
    }

    /**
     * Helper method to retrieve and calculate fluid balance information for a specific date and patient.
     *
     * @param actualDate      the specific date for which to retrieve fluid balance information
     * @param patientId       the ID of the patient whose fluid balance information is to be retrieved
     * @param actualStartDate the start date for filtering records (used for database queries)
     * @param actualEndDate   the end date for filtering records (used for database queries)
     * @return CalculateFluidBalanceResponseDto containing the calculated fluid balance information for the specified date and patient
     */
    private CalculateFluidBalanceResponseDto getBalancesInformation(LocalDateTime actualDate, Long  patientId, LocalDateTime actualStartDate, LocalDateTime actualEndDate) {
        CalculateFluidBalanceResponseDto response = new CalculateFluidBalanceResponseDto();
        response.setFluidBalances(this.getFluidBalanceByDateAndPatient(Objects.nonNull(actualDate)?actualDate:actualStartDate,
                Objects.nonNull(actualEndDate)?actualEndDate:null,patientId));
        if(response.getFluidBalances().isEmpty()){
            return null;
        }
        response.setExtraFluids(extraFluidRepository.getExtraFluidByDateIsBetweenAndPatientId(
                actualStartDate,actualEndDate,patientId).stream().map(ExtraFluidResponseDto::new).toList());
        response.setVitalSignDetails(vitalSignDetailRepository.getVitalSignDetailsByDateIsBetweenAndPatientIdAndStatusOrderByDateAsc(
                actualStartDate,actualEndDate,patientId, StatusEnum.ACTIVO).stream().map(VitalSignDetailResponse::new).toList());
        response.setMedicineDetails(medicineDetailRepository.getMedicineDetailByPatientIdAndStatusOrderByDateAsc(
                patientId,StatusEnum.ACTIVO).stream().map(MedicineDetailResponseDto::new).toList());
        response.setTotalIngested(response.getExtraFluids().stream().map(ExtraFluidResponseDto::getIngested).reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setTotalUrine(response.getExtraFluids().stream().map(ExtraFluidResponseDto::getUrine).reduce(BigDecimal.ZERO, BigDecimal::add).negate());

        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger changeNumber = new AtomicInteger(1);
        response.getFluidBalances().forEach(fluidBalance -> {
            int index = count.getAndIncrement();
            if (index < 1) {
                fluidBalance.setUltrafiltration(fluidBalance.getDrained().negate());
            }else {
                fluidBalance.setUltrafiltration(response.getFluidBalances().get(index-1).getInfused().subtract(response.getFluidBalances().get(index).getDrained()));
            }
            int change = changeNumber.getAndIncrement();
            change = change>=6?1:change;
            fluidBalance.setChangeNumber(change);
        });

        response.setPartialBalance(response.getFluidBalances().stream().map(FluidBalanceResponse::getUltrafiltration).reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setTotalBalance(response.getPartialBalance().add(response.getTotalUrine()));
        response.getFluidBalances().forEach(fluidBalance -> {
            fluidBalance.setTotalUrine(response.getTotalUrine());
            fluidBalance.setTotalIngested(response.getTotalIngested());
            fluidBalance.setPartialBalance(response.getPartialBalance());
            fluidBalance.setTotalBalance(response.getTotalBalance());
        });
        response.setFinalBalance(response.getTotalBalance().add(response.getTotalIngested()));

        return response;
    }

    /**
     * Generates a PDF report of the fluid balance for a patient based on the provided date range.
     *
     * @param patientId the ID of the patient whose fluid balance report is to be generated
     * @param startDate the start date for filtering records to be included in the report
     * @param endDate   the end date for filtering records to be included in the report
     * @return List containing the generated PDF file as a byte array, its size, and a filename
     * @throws BalanceGlobalException if there is an error during report generation or if the patient is not found
     */
    public List<Object> getReportBalanceFluidForPatient(Long patientId, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        List<Object> response = new ArrayList<>();
        Optional<Patient> patient = patientRepository.findById(patientId);
        if(patient.isEmpty()){
            throw new BalanceGlobalException("Error al generar el reporte del paciente: ", HttpStatus.CONFLICT.value());
        }
        List<CalculateFluidBalanceResponseDto> responseDtoList = this.calculateBalanceFluidForPatient(patientId, startDate, endDate);
        List<PdfFileDto> pdfs = new ArrayList<>();

        for (int i = 0; i < responseDtoList.size(); i += 7) {
            int fin = Math.min(i + 7, responseDtoList.size());
            List<CalculateFluidBalanceResponseDto> week = responseDtoList.subList(i, fin);
            try {
                pdfs.add(new PdfFileDto(patient.get().getName()+"-"+(i+1)+"-a-"+ fin +".pdf",
                        reportService.generateReport(week)));
            } catch (Exception e) {
                log.error("Error al generar el reporte",e);
                throw new BalanceGlobalException("Error al generar el reporte: ", HttpStatus.CONFLICT.value());
            }

        }

        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        byte[] file = this.mergePdfs(pdfs);
        response.add(file);
        response.add(file.length);
        response.add(patient.get().getName()+"-"+startDate.format(formatterDay)+"-a-"+endDate.format(formatterDay)+".pdf");
        return response;
    }

    /**
     * Merges multiple PDF files into a single PDF file.
     *
     * @param pdfs a list of PdfFileDto objects containing the name and data of each PDF file to be merged
     * @return a byte array representing the merged PDF file
     * @throws Exception if an error occurs during the merging process
     */
    public byte[] mergePdfs(List<PdfFileDto> pdfs) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument finalDoc = new PdfDocument(new PdfWriter(baos));
        PdfMerger merger = new PdfMerger(finalDoc);
        for (PdfFileDto pdf : pdfs) {
            PdfDocument sourceDoc = new PdfDocument(
                    new PdfReader(new ByteArrayInputStream(pdf.getData()))
            );
            merger.merge(sourceDoc, 1, sourceDoc.getNumberOfPages());
            sourceDoc.close();
        }
        finalDoc.close();

        return baos.toByteArray();
    }

    /**
     * Generates a PDF report of the fluid balance for a patient based on the provided date range and sends it to the patient's email.
     *
     * @param patientId the ID of the patient whose fluid balance report is to be generated and sent
     * @param startDate the start date for filtering records to be included in the report
     * @param endDate   the end date for filtering records to be included in the report
     * @throws Exception if there is an error during report generation or email sending
     */
    public void sendReportBalanceFluidForPatientToEmail(Long patientId, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        List<Object> response = this.getReportBalanceFluidForPatient(patientId, startDate, endDate);
        if (!response.isEmpty()) {
            mailService.sendBalancesMailToUserMail(response,patientId,startDate ,endDate);
        }
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidBalance;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.ExtraFluidRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.FluidBalanceRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MedicineDetailRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.VitalSignDetailRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for managing fluid balance records, including saving new records and retrieving existing ones.
 */
@RequiredArgsConstructor
@Service
public class FluidBalanceService {

    private final FluidBalanceRepository fluidBalanceRepository;
    private final ExtraFluidRepository extraFluidRepository;
    private final VitalSignDetailRepository vitalSignDetailRepository;
    private final MedicineDetailRepository medicineDetailRepository;

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
        Date startDate = Utility.startDay(Utility.convertLocalDateTimeToDate(startLocalDate));
        Date endDate = Objects.nonNull(endLocalDate)?Utility.convertLocalDateTimeToDate(endLocalDate):Utility.endDay(startDate);

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
            Date actualStartDate = Utility.startDay(Utility.convertLocalDateTimeToDate(actualDate));
            Date actualEndDate = Utility.endDay(actualStartDate);
            // Process to calculate fluid balance by actual day
            responseDtoList.add(this.getBalancesInformation(actualDate, patientId, actualStartDate, actualEndDate));
        }else {
            // Process to calculate fluid balance by startDate and endDate range
            for (LocalDateTime dt = startDate; !dt.isAfter(endDate); dt = dt.plusDays(1)) {
                Date actualStartDate = Utility.startDay(Utility.convertLocalDateTimeToDate(dt));
                Date actualEndDate = Utility.endDay(actualStartDate);
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
    private CalculateFluidBalanceResponseDto getBalancesInformation(LocalDateTime actualDate, Long  patientId, Date actualStartDate, Date actualEndDate) {
        CalculateFluidBalanceResponseDto response = new CalculateFluidBalanceResponseDto();
        response.setFluidBalances(this.getFluidBalanceByDateAndPatient(Objects.nonNull(actualDate)?actualDate:Utility.convertDateToLocalDateTime(actualStartDate),
                Objects.nonNull(actualEndDate)?Utility.convertDateToLocalDateTime(actualEndDate):null,patientId));
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
        response.getFluidBalances().forEach(fluidBalance -> {
            int index = count.getAndIncrement();
            if (index < 1) {
                fluidBalance.setUltrafiltration(fluidBalance.getDrained().negate());
            }else {
                fluidBalance.setUltrafiltration(response.getFluidBalances().get(index-1).getInfused().subtract(response.getFluidBalances().get(index).getDrained()));
            }
        });

        response.setPartialBalance(response.getFluidBalances().stream().map(FluidBalanceResponse::getUltrafiltration).reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setTotalBalance(response.getPartialBalance().add(response.getTotalUrine()));
        response.setFinalBalance(response.getTotalBalance().add(response.getTotalIngested()));

        return response;
    }
}

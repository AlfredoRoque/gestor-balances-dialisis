package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.FluidBalanceRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.FluidBalanceResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidBalance;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.FluidBalanceRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Service for managing fluid balance records, including saving new records and retrieving existing ones.
 */
@RequiredArgsConstructor
@Service
public class FluidBalanceService {

    private final FluidBalanceRepository fluidBalanceRepository;

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

        return fluidBalanceRepository.getFluidBalancesByDateBetweenAndPatientId(startDate, endDate,patientId).stream().map(FluidBalanceResponse::new).toList();
    }
}

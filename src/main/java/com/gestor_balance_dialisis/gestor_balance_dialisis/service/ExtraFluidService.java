package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ExtraFluidRequestDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ExtraFluidResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.ExtraFluid;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.ExtraFluidRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service for managing extra fluids, including saving new extra fluid records and retrieving existing ones.
 * This service will handle the business logic related to extra fluids for patients, such as urine and ingested fluids.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExtraFluidService {

    private final ExtraFluidRepository extraFluidRepository;

    /**
     * Saves a new extra fluid record based on the provided request data.
     *
     * @param extraFluidRequestDto the request containing the extra fluid information to be saved
     * @return a response containing the saved extra fluid information
     */
    @Transactional
    public ExtraFluidResponseDto save(ExtraFluidRequestDto extraFluidRequestDto) {
        log.info("patientId : {}",extraFluidRequestDto.getPatientId());
        return new ExtraFluidResponseDto(extraFluidRepository.save(new ExtraFluid(extraFluidRequestDto)));
    }

    /**
     * Retrieves extra fluid records for the actual date and a specific patient.
     *
     * @param patientId the ID of the patient for whom to retrieve extra fluid records
     * @return a list of responses containing the extra fluid information for the specified patient and date
     */
    public List<ExtraFluidResponseDto> getExtraFluidByActualDateAndPatient(Long patientId,Instant actualDate) {
        log.info("patientId: {}",patientId);
        return extraFluidRepository.getExtraFluidByDateIsBetweenAndPatientId(
                        Utility.startDay(actualDate),Utility.endDay(actualDate),patientId)
                .stream().map(ExtraFluidResponseDto::new).toList();
    }

    /**
     * Retrieves extra fluid records for a specific date range and a specific patient.
     *
     * @param patientId      the ID of the patient for whom to retrieve extra fluid records
     * @param startLocalDate the start date of the date range for which to retrieve extra fluid records
     * @param endLocalDate   the end date of the date range for which to retrieve extra fluid records
     * @return a list of responses containing the extra fluid information for the specified patient and date range
     */
    public List<ExtraFluidResponseDto> getExtraFluidByDateAndPatient(Long patientId,Instant startLocalDate, Instant endLocalDate) {
        log.info(" patientId: {}",patientId);
        Instant startDate = Utility.startDay(startLocalDate);
        Instant endDate = Objects.nonNull(endLocalDate)?Utility.endDay(endLocalDate):Utility.endDay(startDate);
        return extraFluidRepository.getExtraFluidByDateIsBetweenAndPatientId(
                        Utility.startDay(startDate),Utility.endDay(endDate),patientId)
                .stream().map(ExtraFluidResponseDto::new).toList();
    }

    /**
     * Updates an existing extra fluid record based on the provided ID and request data.
     *
     * @param extraFluidId        the ID of the extra fluid record to be updated
     * @param extraFluidRequestDto the request containing the updated extra fluid information
     * @return a response containing the updated extra fluid information
     */
    public ExtraFluidResponseDto updateExtraFluid(Long extraFluidId, @Valid ExtraFluidRequestDto extraFluidRequestDto) {
        log.info("extraFluidId : {}",extraFluidId);
        Optional<ExtraFluid> extraFluidUpdate = extraFluidRepository.findById(extraFluidId);
        if (extraFluidUpdate.isPresent()) {
            extraFluidRequestDto.setId(extraFluidId);
            return new ExtraFluidResponseDto(extraFluidRepository.save(new ExtraFluid(extraFluidRequestDto)));
        }
        throw new BalanceGlobalException(String.format(Constants.EXTRA_FLUID_NOT_FOUND, extraFluidId), HttpStatus.CONFLICT.value());
    }

    /**
     * Deletes an extra fluid record based on the provided ID.
     *
     * @param extraFluidId the ID of the extra fluid record to be deleted
     */
    public void deleteExtraFluid(Long extraFluidId) {
        log.info("extraFluidId: {}",extraFluidId);
        Optional<ExtraFluid> extraFluidUpdate = extraFluidRepository.findById(extraFluidId);
        if (extraFluidUpdate.isPresent()) {
            extraFluidRepository.deleteById(extraFluidId);
            return;
        }
        throw new BalanceGlobalException(String.format(Constants.EXTRA_FLUID_NOT_FOUND, extraFluidId), HttpStatus.CONFLICT.value());
    }
}

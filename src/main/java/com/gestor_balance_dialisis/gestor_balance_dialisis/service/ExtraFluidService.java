package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ExtraFluidRequestDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ExtraFluidResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.ExtraFluid;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.ExtraFluidRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Service for managing extra fluids, including saving new extra fluid records and retrieving existing ones.
 * This service will handle the business logic related to extra fluids for patients, such as urine and ingested fluids.
 */
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
        return new ExtraFluidResponseDto(extraFluidRepository.save(new ExtraFluid(extraFluidRequestDto)));
    }

    /**
     * Retrieves extra fluid records for the actual date and a specific patient.
     *
     * @param patientId the ID of the patient for whom to retrieve extra fluid records
     * @return a list of responses containing the extra fluid information for the specified patient and date
     */
    public List<ExtraFluidResponseDto> getExtraFluidByActualDateAndPatient(Long patientId) {
        LocalDateTime actualDate = LocalDateTime.now();
        return extraFluidRepository.getExtraFluidByDateIsBetweenAndPatientId(
                        Utility.startDay(actualDate),Utility.endDay(actualDate),patientId)
                .stream().map(ExtraFluidResponseDto::new).toList();
    }
}

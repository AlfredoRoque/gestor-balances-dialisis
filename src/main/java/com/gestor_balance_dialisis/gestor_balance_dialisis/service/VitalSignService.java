package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSign;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSignDetail;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.VitalSignDetailRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.VitalSignRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing vital sign records, including saving new records and retrieving existing ones.
 */
@RequiredArgsConstructor
@Service
public class VitalSignService {

    private final VitalSignRepository vitalSignRepository;
    private final VitalSignDetailRepository vitalSignDetailRepository;

    /**
     * Saves a new vital sign record based on the provided request data.
     *
     * @param vitalSignRequest the request containing the vital sign information to be saved
     * @return a response containing the saved vital sign information
     */
    @Transactional
    public VitalSignResponse save(VitalSignRequest vitalSignRequest) {
        return new VitalSignResponse(vitalSignRepository.save(new VitalSign(vitalSignRequest)));
    }

    /**
     * Retrieves all vital sign records from the database.
     *
     * @return a list of responses containing the information of all vital signs
     */
    public List<VitalSignResponse> getAllVitalSigns() {
        return vitalSignRepository.findAll()
                .stream().map(VitalSignResponse::new).toList();
    }

    /**
     * Saves a new vital sign detail record based on the provided request data.
     *
     * @param vitalSignDetailRequest the request containing the vital sign detail information to be saved
     * @return a response containing the saved vital sign detail information
     */
    @Transactional
    public VitalSignDetailResponse saveVitalSignDetail(VitalSignDetailRequest vitalSignDetailRequest) {
        return new VitalSignDetailResponse(vitalSignDetailRepository.save(new VitalSignDetail(vitalSignDetailRequest)));
    }

    /**
     * Updates an existing vital sign detail record based on the provided request data.
     *
     * @param vitalSignDetailUpdateRequest the request containing the updated vital sign detail information
     * @return a response containing the updated vital sign detail information
     * @throws BalanceGlobalException if the vital sign detail record to be updated does not exist
     */
    @Transactional
    public VitalSignDetailResponse updateVitalSignDetail(VitalSignDetailUpdateRequest vitalSignDetailUpdateRequest) {
        Optional<VitalSignDetail> vitalSignDetail = vitalSignDetailRepository.findById(vitalSignDetailUpdateRequest.getId());
        if(vitalSignDetail.isPresent()){
            return new VitalSignDetailResponse(vitalSignDetailRepository.save(new VitalSignDetail(vitalSignDetail.get(), vitalSignDetailUpdateRequest)));
        }
        throw new BalanceGlobalException("Vital sign detail doesn't exist", HttpStatus.CONFLICT.value());
    }

    /**
     * Retrieves a list of vital sign detail records for a specific patient based on the actual date and status 'ACTIVO'.
     *
     * @param patientId the ID of the patient for whom to retrieve the vital sign details
     * @return a list of responses containing the information of the vital sign details for the specified patient and date
     */
    public List<VitalSignDetailResponse> getVitalSignDetailByActualDateAndPatient(Long patientId) {
        LocalDateTime actualDate = LocalDateTime.now();
        return vitalSignDetailRepository.getVitalSignDetailsByDateIsBetweenAndPatientIdAndStatusOrderByDateAsc(
                Utility.startDay(actualDate),Utility.endDay(actualDate),patientId, StatusEnum.ACTIVO)
                .stream().map(VitalSignDetailResponse::new).toList();
    }

    /**
     * Updates an existing vital sign record based on the provided request data and vital sign ID.
     *
     * @param vitalSignId      the ID of the vital sign to be updated
     * @param vitalSignRequest the request containing the updated vital sign information
     * @return a response containing the updated vital sign information
     * @throws BalanceGlobalException if the vital sign record to be updated does not exist
     */
    @Transactional
    public VitalSignResponse updateVitalSign(Long vitalSignId, VitalSignRequest vitalSignRequest) {
        Optional<VitalSign> vitalSign = vitalSignRepository.findById(vitalSignId);
        if(vitalSign.isPresent()){
            vitalSignRequest.setId(vitalSignId);
            return new VitalSignResponse(vitalSignRepository.save(new VitalSign(vitalSignRequest)));
        }
        throw new BalanceGlobalException("Vital sign detail doesn't exist", HttpStatus.CONFLICT.value());
    }

    /**
     * Deletes an existing vital sign record based on the provided vital sign ID.
     *
     * @param vitalSignId the ID of the vital sign to be deleted
     * @throws BalanceGlobalException if the vital sign record to be deleted does not exist
     */
    @Transactional
    public void deleteVitalSign(Long vitalSignId) {
        Optional<VitalSign> vitalSign = vitalSignRepository.findById(vitalSignId);
        if(vitalSign.isPresent()){
            vitalSignRepository.deleteById(vitalSignId);
            return;
        }
        throw new BalanceGlobalException("Vital sign detail doesn't exist", HttpStatus.CONFLICT.value());
    }
}

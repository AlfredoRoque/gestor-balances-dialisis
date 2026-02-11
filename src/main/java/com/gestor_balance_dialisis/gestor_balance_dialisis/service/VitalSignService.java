package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSign;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSignDetail;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.VitalSignDetailRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.VitalSignRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
}

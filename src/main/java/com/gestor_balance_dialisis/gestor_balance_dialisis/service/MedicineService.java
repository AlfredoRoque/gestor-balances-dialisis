package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Medicine;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.MedicineDetail;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MedicineDetailRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MedicineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing medicine records, including saving new records and retrieving existing ones.
 */
@RequiredArgsConstructor
@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineDetailRepository medicineDetailRepository;

    /**
     * Saves a new medicine record based on the provided request data.
     *
     * @param medicineRequest the request containing the medicine information to be saved
     * @return a response containing the saved medicine information
     */
    @Transactional
    public MedicineResponse save(MedicineRequest medicineRequest) {
        return new MedicineResponse(medicineRepository.save(new Medicine(medicineRequest)));
    }

    /**
     * Retrieves all medicine records from the database.
     *
     * @return a list of responses containing the information of all medicines
     */
    public List<MedicineResponse> getAllMedicines() {
        return medicineRepository.findAll()
                .stream().map(MedicineResponse::new).toList();
    }

    /**
     * Saves a new medicine detail record based on the provided request data.
     *
     * @param medicineDetailRequest the request containing the medicine detail information to be saved
     * @return a response containing the saved medicine detail information
     */
    @Transactional
    public MedicineDetailResponseDto saveMedicineDetail(MedicineDetailRequestDto medicineDetailRequest) {
        return new MedicineDetailResponseDto(medicineDetailRepository.save(new MedicineDetail(medicineDetailRequest)));
    }

    /**
     * Updates an existing medicine detail record based on the provided request data.
     *
     * @param medicineDetailUpdateRequestDto the request containing the updated medicine detail information
     * @return a response containing the updated medicine detail information
     * @throws BalanceGlobalException if the medicine detail to be updated does not exist
     */
    @Transactional
    public MedicineDetailResponseDto updateMedicineDetail(MedicineDetailUpdateRequestDto medicineDetailUpdateRequestDto) {
        Optional<MedicineDetail> medicineDetail = medicineDetailRepository.findById(medicineDetailUpdateRequestDto.getId());
        if(medicineDetail.isPresent()){
            return new MedicineDetailResponseDto(medicineDetailRepository.save(new MedicineDetail(
                    medicineDetail.get(), medicineDetailUpdateRequestDto, new Date())));
        }
        throw new BalanceGlobalException("Medicine detail doesn't exist", HttpStatus.CONFLICT.value());
    }

    /**
     * Retrieves the medicine details for a specific patient based on the provided patient ID.
     *
     * @param patientId the ID of the patient for whom to retrieve the medicine details
     * @return a list of responses containing the information of the medicine details for the specified patient
     */
    public List<MedicineDetailResponseDto> getVitalSignDetailByPatient(Long patientId) {
        return medicineDetailRepository.getMedicineDetailByPatientIdAndStatusOrderByDateAsc(patientId, StatusEnum.ACTIVO)
                .stream().map(MedicineDetailResponseDto::new).toList();
    }
}

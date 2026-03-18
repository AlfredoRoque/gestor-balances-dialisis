package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Medicine;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.MedicineDetail;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MedicineDetailRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MedicineRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing medicine records, including saving new records and retrieving existing ones.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineDetailRepository medicineDetailRepository;
    private final SubscriptionService subscriptionService;

    /**
     * Saves a new medicine record based on the provided request data.
     *
     * @param medicineRequest the request containing the medicine information to be saved
     * @return a response containing the saved medicine information
     */
    @Transactional
    public MedicineResponse save(MedicineRequest medicineRequest) {
        log.info(" medicine : {}",medicineRequest.getName());
        SubscriptionDto subs = subscriptionService.getSubscription(medicineRequest.getUserId());
        if (!Utility.isSpecialPlan(subs.getPlan().getName())) {
            if (medicineRepository.countByUserId(SecurityUtils.getUserId())>=subs.getPlan().getParametersPlan().getMaxMedicines()) {
                throw new BalanceGlobalException(String.format(Constants.MEDICINES_PLAN_LIMIT,
                        subs.getPlan().getParametersPlan().getMaxMedicines(), subs.getPlan().getName(), subs.getPlan().getParametersPlan().getMaxMedicines()), HttpStatus.CONFLICT.value());
            }
        }

        return new MedicineResponse(medicineRepository.save(new Medicine(medicineRequest)));
    }

    /**
     * Retrieves all medicine records from the database.
     *
     * @return a list of responses containing the information of all medicines
     */
    public List<MedicineResponse> getAllMedicinesByUser() {
        return medicineRepository.getAllMedicinesByUserId(SecurityUtils.getUserId())
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
        log.info(" medicineId : {}",medicineDetailRequest.getMedicine().getId());
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
    public MedicineDetailResponseDto updateMedicineDetail(Long medicineId, MedicineDetailRequestDto medicineDetailUpdateRequestDto) {
        log.info("medicineId : {}",medicineId);
        Optional<MedicineDetail> medicineDetail = medicineDetailRepository.findById(medicineId);
        if(medicineDetail.isPresent()){
            ZoneId zone = SecurityUtils.getUserZone();
            return new MedicineDetailResponseDto(medicineDetailRepository.save(new MedicineDetail(
                    medicineDetail.get(), medicineDetailUpdateRequestDto, Instant.now().atZone(zone).toInstant())));
        }
        throw new BalanceGlobalException(Constants.MEDICINE_DETAIL_NOT_FOUND, HttpStatus.CONFLICT.value());
    }

    /**
     * Deletes an existing medicine detail record based on the provided medicine ID.
     *
     * @param medicineId the ID of the medicine detail to be deleted
     * @throws BalanceGlobalException if the medicine detail to be deleted does not exist
     */
    @Transactional
    public void deleteMedicineDetail(Long medicineId) {
        log.info(" medicineId: {}",medicineId);
        Optional<MedicineDetail> medicineDetail = medicineDetailRepository.findById(medicineId);
        if(medicineDetail.isPresent()){
            medicineDetailRepository.deleteById(medicineId);
            return;
        }
        throw new BalanceGlobalException(Constants.MEDICINE_DETAIL_NOT_FOUND, HttpStatus.CONFLICT.value());
    }

    /**
     * Retrieves the medicine details for a specific patient based on the provided patient ID.
     *
     * @param patientId the ID of the patient for whom to retrieve the medicine details
     * @return a list of responses containing the information of the medicine details for the specified patient
     */
    public List<MedicineDetailResponseDto> getVitalSignDetailByPatient(Long patientId) {
        log.info(" patientId : {}",patientId);
        return medicineDetailRepository.getMedicineDetailByPatientIdAndStatusOrderByDateAsc(patientId, StatusEnum.ACTIVO)
                .stream().map(MedicineDetailResponseDto::new).toList();
    }

    /**
     * Updates an existing medicine record based on the provided medicine ID and request data.
     *
     * @param medicineId      the ID of the medicine to be updated
     * @param medicineRequest the request containing the updated medicine information
     * @return a response containing the updated medicine information
     * @throws BalanceGlobalException if the medicine to be updated does not exist
     */
    @Transactional
    public MedicineResponse updateMedicine(Long medicineId, MedicineRequest medicineRequest) {
        log.info(" medicine Id :{} ",medicineId);
        Optional<Medicine> medicine = medicineRepository.findById(medicineId);
        if(medicine.isPresent()){
            medicineRequest.setId(medicineId);
            return new MedicineResponse(medicineRepository.save(new Medicine(medicineRequest)));
        }
        throw new BalanceGlobalException(Constants.MEDICINE_NOT_FOUND, HttpStatus.CONFLICT.value());
    }

    /**
     * Deletes an existing medicine record based on the provided medicine ID.
     *
     * @param medicineId the ID of the medicine to be deleted
     * @throws BalanceGlobalException if the medicine to be deleted does not exist
     */
    @Transactional
    public void deleteMedicine(Long medicineId) {
        log.info(" medicine ID : {}",medicineId);
        Optional<Medicine> medicine = medicineRepository.findById(medicineId);
        if(medicine.isPresent()){
            medicineRepository.deleteById(medicineId);
            return;
        }
        throw new BalanceGlobalException(Constants.MEDICINE_NOT_FOUND, HttpStatus.CONFLICT.value());
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PatientRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PatientResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.PatientRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.RsaKeyService;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing patient records, including saving new patients and retrieving existing ones by user ID.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final RsaKeyService rsaKeyService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Save a new patient record in the system, returns the saved patient response.
     *
     * @param patientRequest The patient request containing the patient's information to be saved.
     * @return The saved patient response.
     */
    @Transactional
    public PatientResponse save(PatientRequest patientRequest) {
        log.info(" userId : {}",patientRequest.getUserId());
        if (patientRepository.findByName(patientRequest.getName()).isPresent()) {
            throw new BalanceGlobalException(Constants.PATIENT_ALREADY_EXIST, HttpStatus.CONFLICT.value());
        }
        String rawPassword = SecurityUtils.decryptPassword(patientRequest.getPassword(),rsaKeyService);
        return new PatientResponse(patientRepository.save(new Patient(patientRequest,passwordEncoder.encode(rawPassword))));
    }

    /**
     * Find patients by user ID, returns a list of patient responses associated with the specified user ID.
     *
     * @param userId The ID of the user whose patients are to be retrieved.
     * @return A list of patient responses associated with the specified user ID.
     */
    public List<PatientResponse> findByUserId(Long userId) {
        log.info("userId : {}",userId);
        return patientRepository.findByUserId(userId)
                .stream().map(PatientResponse::new).toList();
    }

    /**
     * Update an existing patient record in the system, returns the updated patient response.
     *
     * @param patientRequest The patient request containing the updated patient's information.
     * @param patientId      The ID of the patient to be updated.
     * @return The updated patient response.
     */
    @Transactional
    public PatientResponse updatePatient(PatientRequest patientRequest,Long patientId) {
        log.info(" patientId : {}",patientId);
        patientRequest.setId(patientId);
        if(patientRequest.getPassword().equals(Constants.SAME_PASSWORD)){
            Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new BalanceGlobalException(Constants.PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND.value()));
            patientRequest.setPassword(patient.getPassword());
            return new PatientResponse(patientRepository.save(new Patient(patientRequest)));
        }
        String newRawPassword = SecurityUtils.decryptPassword(patientRequest.getPassword(),rsaKeyService);
        return new PatientResponse(patientRepository.save(new Patient(patientRequest,passwordEncoder.encode(newRawPassword))));
    }

    /**
     * Delete a patient record from the system based on the provided patient ID.
     *
     * @param patientId The ID of the patient to be deleted.
     */
    @Transactional
    public void deletePatient(Long patientId) {
        log.info("patientId : {}",patientId);
        patientRepository.deleteById(patientId);
    }

    /**
     * Find a patient by their ID, returns the patient response if found.
     *
     * @param patientId The ID of the patient to be retrieved.
     * @return The patient response if the patient is found.
     * @throws BalanceGlobalException if the patient is not found.
     */
    public PatientResponse findById(Long patientId) {
        log.info("patientId: {}",patientId);
        Optional<Patient> patient = patientRepository.findById(patientId);
        patient.orElseThrow(() -> new BalanceGlobalException(Constants.PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND.value()));
        return new PatientResponse(patient.get());
    }
}

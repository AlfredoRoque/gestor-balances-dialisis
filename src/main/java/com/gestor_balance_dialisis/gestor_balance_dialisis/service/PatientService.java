package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PatientRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PatientResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing patient records, including saving new patients and retrieving existing ones by user ID.
 */
@RequiredArgsConstructor
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Save a new patient record in the system, returns the saved patient response.
     *
     * @param patientRequest The patient request containing the patient's information to be saved.
     * @return The saved patient response.
     */
    @Transactional
    public PatientResponse save(PatientRequest patientRequest) {
        return new PatientResponse(patientRepository.save(new Patient(patientRequest)));
    }

    /**
     * Find patients by user ID, returns a list of patient responses associated with the specified user ID.
     *
     * @param userId The ID of the user whose patients are to be retrieved.
     * @return A list of patient responses associated with the specified user ID.
     */
    public List<PatientResponse> findByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .stream().map(PatientResponse::new).toList();
    }
}

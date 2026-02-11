package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.MedicineRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.MedicineResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Medicine;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MedicineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing medicine records, including saving new records and retrieving existing ones.
 */
@RequiredArgsConstructor
@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

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
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controller for managing medicines, including saving new medicines and retrieving existing ones.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Medicine controller", description = "Service for medicine management.")
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    /**
     * Endpoint to save a new medicine with the provided information.
     *
     * @param medicineRequest the request containing the medicine information to be saved
     * @return a response containing the saved medicine information
     */
    @Operation(summary = "Save a new medicine", description = "Endpoint to save a new medicine with the provided information.")
    @PostMapping("/save")
    public ResponseEntity<MedicineResponse> saveMedicine(@Valid @RequestBody MedicineRequest medicineRequest) {
        return ResponseEntity.created(URI.create("/api/medicines/save" + medicineRequest.getId())).body(medicineService.save(medicineRequest));
    }

    /**
     * Endpoint to retrieve all medicines available in the system.
     *
     * @return a list of responses containing the information of all medicines
     */
    @Operation(summary = "Get all Medicines", description = "Endpoint to retrieve all medicines available in the system.")
    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    /**
     * Endpoint to save a new medicine detail with the provided information.
     *
     * @param medicineDetailRequestDto the request containing the medicine detail information to be saved
     * @return a response containing the saved medicine detail information
     */
    @Operation(summary = "Save medicine detail", description = "Endpoint to save a new medicine detail with the provided information.")
    @PostMapping("/details/save")
    public ResponseEntity<MedicineDetailResponseDto> saveVitalSignDetail(@Valid @RequestBody MedicineDetailRequestDto medicineDetailRequestDto) {
        return ResponseEntity.created(URI.create("/api/medicines/details/save")).body(medicineService.saveMedicineDetail(medicineDetailRequestDto));
    }

    /**
     * Endpoint to update a medicine detail with the provided information.
     *
     * @param medicineDetailRequestDto the request containing the medicine detail information to be updated
     * @return a response containing the updated medicine detail information
     */
    @Operation(summary = "Update medicine detail", description = "Endpoint to update a medicine detail with the provided information.")
    @PatchMapping("/details/update")
    public ResponseEntity<MedicineDetailResponseDto> updateVitalSignDetail(@Valid @RequestBody MedicineDetailUpdateRequestDto medicineDetailRequestDto) {
        return ResponseEntity.ok(medicineService.updateMedicineDetail(medicineDetailRequestDto));
    }

    /**
     * Endpoint to retrieve the medicine details of a patient.
     *
     * @param patientId the ID of the patient whose medicine details are to be retrieved
     * @return a list of responses containing the medicine details of the specified patient
     */
    @Operation(summary = "Get medicine details by patient", description = "Endpoint to retrieve the medicine details of a patient.")
    @GetMapping("/details/patients/{patientId}")
    public ResponseEntity<List<MedicineDetailResponseDto>> getVitalSignDetailByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicineService.getVitalSignDetailByPatient(patientId));
    }
}

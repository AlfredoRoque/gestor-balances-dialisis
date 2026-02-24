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
     * Endpoint to retrieve all medicines for the current user.
     *
     * @return a list of responses containing the information of all medicines belonging to the user
     */
    @Operation(summary = "Get all Medicines", description = "Endpoint to retrieve all medicines available in the system.")
    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAllMedicinesByUser() {
        return ResponseEntity.ok(medicineService.getAllMedicinesByUser());
    }

    /**
     * Endpoint to update an existing medicine with the provided information.
     *
     * @param medicineId      the ID of the medicine to be updated
     * @param medicineRequest the request containing the updated medicine information
     * @return a response containing the updated medicine information
     */
    @Operation(summary = "Update a medicine", description = "Endpoint to update an existing medicine with the provided information.")
    @PatchMapping("/{medicineId}")
    public ResponseEntity<MedicineResponse> updateMedicine(@PathVariable Long medicineId, @Valid @RequestBody MedicineRequest medicineRequest) {
        return ResponseEntity.ok(medicineService.updateMedicine(medicineId,medicineRequest));
    }

    /**
     * Endpoint to delete an existing medicine with the provided medicine ID.
     *
     * @param medicineId the ID of the medicine to be deleted
     * @return a response indicating that the medicine has been successfully deleted
     */
    @Operation(summary = "Delete a medicine", description = "Endpoint to delete an existing medicine with the provided medicine ID.")
    @DeleteMapping("/{medicineId}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long medicineId) {
        medicineService.deleteMedicine(medicineId);
        return ResponseEntity.noContent().build();
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
    @PatchMapping("/details/{medicineDetailId}")
    public ResponseEntity<MedicineDetailResponseDto> updateVitalSignDetail(@Valid @RequestBody MedicineDetailRequestDto medicineDetailRequestDto,
                                                                           @PathVariable Long medicineDetailId) {
        return ResponseEntity.ok(medicineService.updateMedicineDetail(medicineDetailId, medicineDetailRequestDto));
    }

    /**
     * Endpoint to delete a medicine detail with the provided information.
     *
     * @param medicineDetailId the ID of the medicine detail to be deleted
     * @return a response containing the information of the deleted medicine detail
     */
    @Operation(summary = "Delete medicine detail", description = "Endpoint to delete a medicine detail with the provided information.")
    @DeleteMapping("/details/{medicineDetailId}")
    public ResponseEntity<Void> deleteVitalSignDetail(@PathVariable Long medicineDetailId) {
        medicineService.deleteMedicineDetail(medicineDetailId);
        return ResponseEntity.noContent().build();
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

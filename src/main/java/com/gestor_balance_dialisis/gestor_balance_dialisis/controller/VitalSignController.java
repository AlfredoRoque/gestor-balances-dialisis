package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.VitalSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controller for managing vital signs, including saving new vital signs and retrieving existing ones.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Vital signs controller", description = "Service for vital signs management.")
@RequestMapping("/api/vital-signs")
public class VitalSignController {

    private final VitalSignService vitalSignService;

    /**
     * Endpoint to save a new vital sign with the provided information.
     *
     * @param vitalSignRequest The request body containing the details of the vital sign to be saved.
     * @return A ResponseEntity containing the saved vital sign response and an HTTP status code.
     */
    @Operation(summary = "Save a new vital sign", description = "Endpoint to save a new vital sign with the provided information.")
    @PostMapping("/save")
    public ResponseEntity<VitalSignResponse> saveVitalSign(@Valid @RequestBody VitalSignRequest vitalSignRequest) {
        return ResponseEntity.created(URI.create("/api/vital-signs/save")).body(vitalSignService.save(vitalSignRequest));
    }

    /**
     * Endpoint to retrieve all vital signs available in the system.
     *
     * @return A ResponseEntity containing a list of vital sign responses and an HTTP status code.
     */
    @Operation(summary = "Get all vital signs", description = "Endpoint to retrieve all vital signs available in the system.")
    @GetMapping
    public ResponseEntity<List<VitalSignResponse>> getAllVitalSigns() {
        return ResponseEntity.ok(vitalSignService.getAllVitalSigns());
    }

    /**
     * Endpoint to save a new vital sign detail with the provided information.
     *
     * @param vitalSignDetailRequest The request body containing the details of the vital sign detail to be saved.
     * @return A ResponseEntity containing the saved vital sign detail response and an HTTP status code.
     */
    @Operation(summary = "Save vital sign detail", description = "Endpoint to save a new vital sign detail with the provided information.")
    @PostMapping("/details/save")
    public ResponseEntity<VitalSignDetailResponse> saveVitalSignDetail(@Valid @RequestBody VitalSignDetailRequest vitalSignDetailRequest) {
        return ResponseEntity.created(URI.create("/api/vital-signs/details/save")).body(vitalSignService.saveVitalSignDetail(vitalSignDetailRequest));
    }

    /**
     * Endpoint to update an existing vital sign detail with the provided information.
     *
     * @param vitalSignDetailUpdateRequest The request body containing the updated details of the vital sign detail.
     * @return A ResponseEntity containing the updated vital sign detail response and an HTTP status code.
     */
    @Operation(summary = "Update vital sign detail", description = "Endpoint to update a vital sign detail with the provided information.")
    @PatchMapping("/details/update")
    public ResponseEntity<VitalSignDetailResponse> updateVitalSignDetail(@Valid @RequestBody VitalSignDetailUpdateRequest vitalSignDetailUpdateRequest) {
        return ResponseEntity.ok(vitalSignService.updateVitalSignDetail(vitalSignDetailUpdateRequest));
    }

    /**
     * Endpoint to retrieve vital sign details for a specific patient based on the actual date.
     *
     * @param patientId The ID of the patient for whom to retrieve the vital sign details.
     * @return A ResponseEntity containing a list of vital sign detail responses and an HTTP status code.
     */
    @Operation(summary = "Get vital sign detail for actual date and patient", description = "Endpoint to retrieve vital sign details for a specific patient based on the actual date.")
    @GetMapping("/details/patients/actual-date/{patientId}")
    public ResponseEntity<List<VitalSignDetailResponse>> getVitalSignDetailByActualDateAndPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(vitalSignService.getVitalSignDetailByActualDateAndPatient(patientId));
    }
}

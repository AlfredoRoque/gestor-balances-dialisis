package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.VitalSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
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
    public ResponseEntity<List<VitalSignResponse>> getAllVitalSignsByUser() {
        return ResponseEntity.ok(vitalSignService.getAllVitalSignsByUser());
    }

    /**
     * Endpoint to update an existing vital sign with the provided information.
     *
     * @param vitalSignId      The ID of the vital sign to be updated.
     * @param vitalSignRequest The request body containing the updated details of the vital sign.
     * @return A ResponseEntity containing the updated vital sign response and an HTTP status code.
     */
    @Operation(summary = "Update vital sign", description = "Endpoint to update a vital sign information.")
    @PatchMapping("/{vitalSignId}")
    public ResponseEntity<VitalSignResponse> updateVitalSign(@Valid @RequestBody VitalSignRequest vitalSignRequest, @PathVariable Long vitalSignId) {
        return ResponseEntity.ok(vitalSignService.updateVitalSign(vitalSignId, vitalSignRequest));
    }

    /**
     * Endpoint to delete an existing vital sign based on the provided ID.
     *
     * @param vitalSignId The ID of the vital sign to be deleted.
     * @return A ResponseEntity with no content and an HTTP status code indicating the result of the deletion operation.
     */
    @Operation(summary = "Update vital sign", description = "Endpoint to update a vital sign information.")
    @DeleteMapping("/{vitalSignId}")
    public ResponseEntity<Void> deleteVitalSign(@PathVariable Long vitalSignId) {
        vitalSignService.deleteVitalSign(vitalSignId);
        return ResponseEntity.noContent().build();
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
    @PatchMapping("/details/{vitalSignDetailId}")
    public ResponseEntity<VitalSignDetailResponse> updateVitalSignDetail(@Valid @RequestBody VitalSignDetailRequest vitalSignDetailUpdateRequest,
                                                                         @PathVariable Long vitalSignDetailId) {
        return ResponseEntity.ok(vitalSignService.updateVitalSignDetail(vitalSignDetailUpdateRequest, vitalSignDetailId));
    }

    /**
     * Endpoint to delete an existing vital sign detail based on the provided ID.
     *
     * @param vitalSignDetailId The ID of the vital sign detail to be deleted.
     * @return A ResponseEntity with no content and an HTTP status code indicating the result of the deletion operation.
     */
    @Operation(summary = "Delete vital sign detail", description = "Endpoint to Delete a vital sign detail with the provided information.")
    @DeleteMapping("/details/{vitalSignDetailId}")
    public ResponseEntity<Void> deleteVitalSignDetail(@PathVariable Long vitalSignDetailId) {
        vitalSignService.deleteVitalSignDetail(vitalSignDetailId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to retrieve vital sign details for a specific patient based on the actual date.
     *
     * @param patientId The ID of the patient for whom to retrieve the vital sign details.
     * @return A ResponseEntity containing a list of vital sign detail responses and an HTTP status code.
     */
    @Operation(summary = "Get vital sign detail for actual date and patient", description = "Endpoint to retrieve vital sign details for a specific patient based on the actual date.")
    @GetMapping("/details/patients/{patientId}/actual-date")
    public ResponseEntity<List<VitalSignDetailResponse>> getVitalSignDetailByActualDateAndPatient(@PathVariable Long patientId,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Instant actualDate) {
        return ResponseEntity.ok(vitalSignService.getVitalSignDetailByActualDateAndPatient(patientId,actualDate));
    }

    /**
     * Endpoint to retrieve vital sign details for a specific patient based on a date range.
     *
     * @param patientId The ID of the patient for whom to retrieve the vital sign details.
     * @param startDate The start date of the date range for which to retrieve the vital sign details.
     * @param endDate   The end date of the date range for which to retrieve the vital sign details (optional).
     * @return A ResponseEntity containing a list of vital sign detail responses and an HTTP status code.
     */
    @Operation(summary = "Get vital sign detail for actual date and patient", description = "Endpoint to retrieve vital sign details for a specific patient based on the actual date.")
    @GetMapping("/details/patients/{patientId}/dates")
    public ResponseEntity<List<VitalSignDetailResponse>> getVitalSignDetailByDatesAndPatient(@PathVariable Long patientId,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Instant startDate,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return ResponseEntity.ok(vitalSignService.getVitalSignDetailByDatesAndPatient(patientId,startDate,endDate));
    }
}

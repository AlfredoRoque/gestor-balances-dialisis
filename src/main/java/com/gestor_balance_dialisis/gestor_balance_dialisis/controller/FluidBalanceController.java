package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.CalculateFluidBalanceResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.FluidBalanceRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.FluidBalanceResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.FluidBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing fluid balance records, providing endpoints for saving new records and retrieving existing ones based on date filters.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Fluid balance controller", description = "Service for fluid balance management.")
@RequestMapping("/api/fluid-balances")
public class FluidBalanceController {

    private final FluidBalanceService fluidBalanceService;

    /**
     * Endpoint to save a new fluid balance record.
     *
     * @param fluidBalanceRequest the fluid balance request containing the record's information
     * @return ResponseEntity containing the saved fluid balance response
     */
    @Operation(summary = "Save a new fluid balance", description = "Endpoint to save a new fluid balance record with the provided information.")
    @PostMapping("/save")
    public ResponseEntity<FluidBalanceResponse> saveFluidBalance(@Valid @RequestBody FluidBalanceRequest fluidBalanceRequest) {
        return ResponseEntity.created(URI.create("/fluid-balances/save")).body(fluidBalanceService.save(fluidBalanceRequest));
    }

    /**
     * Endpoint to retrieve fluid balance records based on the provided date range and patient ID.
     * The end date is optional; if not provided, it will default to the end of the day of the start date.
     *
     * @param startDate the start date for filtering fluid balance records (required)
     * @param endDate   the end date for filtering fluid balance records (optional)
     * @param patientId the ID of the patient whose fluid balance records are to be retrieved (required)
     * @return ResponseEntity containing a list of fluid balance responses matching the specified criteria
     */
    @Operation(summary = "Get fluid balances by dates and patient", description = "Endpoint to retrieve fluid balance records based on the provided date range and patient ID. The end date is optional; " +
            "if not provided, it will default to the end of the day of the start date.")
    @GetMapping("/dates")
    public ResponseEntity<List<FluidBalanceResponse>> getFluidBalanceByDateAndPatient(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(required = false) LocalDateTime endDate,
                                                                            @RequestParam Long patientId) {
        return ResponseEntity.ok(fluidBalanceService.getFluidBalanceByDateAndPatient(startDate,endDate,patientId));
    }

    /**
     * Endpoint to calculate the fluid balance for a patient based on the provided date range and patient ID.
     * If the date range is not provided, it will calculate the fluid balance for all records of the patient.
     *
     * @param patientId the ID of the patient whose fluid balance is to be calculated (required)
     * @return ResponseEntity containing a list of CalculateFluidBalanceResponseDto with the calculated fluid balance for the specified patient
     */
    @Operation(summary = "Calculate fluid balance for patient", description = "Endpoint to calculate the fluid balance for a patient based on the provided date range and patient ID.")
    @GetMapping("/calculate/patients/{patientId}")
    public ResponseEntity<List<CalculateFluidBalanceResponseDto>> calculateBalanceFluidForPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(fluidBalanceService.calculateBalanceFluidForPatient(patientId, null, null));
    }

    /**
     * Endpoint to calculate the fluid balance for a patient based on the provided date range and patient ID.
     *
     * @param startDate the start date for filtering records to be included in the fluid balance calculation (required)
     * @param endDate   the end date for filtering records to be included in the fluid balance calculation (required)
     * @param patientId the ID of the patient whose fluid balance is to be calculated (required)
     * @return ResponseEntity containing a list of CalculateFluidBalanceResponseDto with the calculated fluid balance for the specified patient and date range
     */
    @Operation(summary = "Calculate fluid balance for patient", description = "Endpoint to calculate the fluid balance for a patient based on the provided date range and patient ID.")
    @GetMapping("/calculate/patients/{patientId}/dates")
    public ResponseEntity<List<CalculateFluidBalanceResponseDto>> calculateBalanceFluidForPatientAndDates(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                                            @PathVariable Long patientId) {
        return ResponseEntity.ok(fluidBalanceService.calculateBalanceFluidForPatient(patientId, startDate, endDate));
    }
}

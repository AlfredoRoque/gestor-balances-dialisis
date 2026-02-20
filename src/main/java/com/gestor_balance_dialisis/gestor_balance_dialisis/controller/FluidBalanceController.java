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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
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
     * Endpoint to update an existing fluid balance record.
     *
     * @param fluidBalanceId      the ID of the fluid balance record to be updated
     * @param fluidBalanceRequest the fluid balance request containing the updated information
     * @return ResponseEntity containing the updated fluid balance response
     */
    @Operation(summary = "Update a fluid balance", description = "Endpoint to update an existing fluid balance record with the provided information.")
    @PatchMapping("/{fluidBalanceId}")
    public ResponseEntity<FluidBalanceResponse> updateFluidBalance(@PathVariable Long fluidBalanceId, @Valid @RequestBody FluidBalanceRequest fluidBalanceRequest) {
        return ResponseEntity.ok(fluidBalanceService.updateFluidBalance(fluidBalanceId,fluidBalanceRequest));
    }

    /**
     * Endpoint to delete an existing fluid balance record based on the provided fluid balance ID.
     *
     * @param fluidBalanceId the ID of the fluid balance record to be deleted
     * @return ResponseEntity with no content, indicating that the deletion was successful
     */
    @Operation(summary = "Delete a fluid balance", description = "Endpoint to delete an existing fluid balance record based on the provided fluid balance ID.")
    @DeleteMapping("/{fluidBalanceId}")
    public ResponseEntity<Void> deleteFluidBalance(@PathVariable Long fluidBalanceId) {
        fluidBalanceService.deleteFluidBalance(fluidBalanceId);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<FluidBalanceResponse>> getFluidBalanceByDateAndPatient(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
                                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(required = false) Instant endDate,
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
    public ResponseEntity<List<CalculateFluidBalanceResponseDto>> calculateBalanceFluidForPatient(@PathVariable Long patientId, @RequestParam Instant startDate) {
        return ResponseEntity.ok(fluidBalanceService.calculateBalanceFluidForPatient(patientId, startDate, null));
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
    public ResponseEntity<List<CalculateFluidBalanceResponseDto>> calculateBalanceFluidForPatientAndDates(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
                                                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
                                                                                            @PathVariable Long patientId) {
        return ResponseEntity.ok(fluidBalanceService.calculateBalanceFluidForPatient(patientId, startDate, endDate));
    }

    /**
     * Endpoint to generate a PDF report with the calculated fluid balance for a patient based on the provided date range and patient ID.
     *
     * @param startDate the start date for filtering records to be included in the fluid balance calculation (optional)
     * @param endDate   the end date for filtering records to be included in the fluid balance calculation (optional)
     * @param patientId the ID of the patient for whom the PDF report is to be generated (required)
     * @return ResponseEntity containing the generated PDF report as a byte array, along with appropriate headers for file download
     * @throws Exception if an error occurs during PDF generation
     */
    @Operation(summary = "Generate pdf whit calculate fluid balance for patient", description = "Endpoint to generate a PDF report with the calculated fluid balance for a patient based on the provided date range and patient ID.")
    @GetMapping("/reports/balances/patients/{patientId}/dates")
    public ResponseEntity<byte[]> getReportBalanceFluidForPatient(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
                                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
                                                                   @PathVariable Long patientId) throws Exception {
        List<Object> response = fluidBalanceService.getReportBalanceFluidForPatient(patientId, startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+response.get(2))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength((Integer) response.get(1))
                .body((byte[]) response.get(0));
    }

    /**
     * Endpoint to generate a PDF report with the calculated fluid balance for a patient based on the provided date range and patient ID, and send it to the patient's email.
     *
     * @param startDate the start date for filtering records to be included in the fluid balance calculation (optional)
     * @param endDate   the end date for filtering records to be included in the fluid balance calculation (optional)
     * @param patientId the ID of the patient for whom the PDF report is to be generated and sent via email (required)
     * @return ResponseEntity with no content, indicating that the email has been sent successfully
     * @throws Exception if an error occurs during PDF generation or email sending
     */
    @Operation(summary = "Generate pdf whit calculate fluid balance for patient and sed to email", description = "Endpoint to generate a PDF report with the calculated fluid balance for a patient based on the provided date range and patient ID, and send it to the patient's email.")
    @GetMapping("/reports/balances/patients/{patientId}/dates/email")
    public ResponseEntity<Void> sendReportBalanceFluidForPatientToEmail(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
                                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
                                                                  @PathVariable Long patientId) throws Exception {
        fluidBalanceService.sendReportBalanceFluidForPatientToEmail(patientId, startDate, endDate);
        return ResponseEntity.noContent().build();
    }
}

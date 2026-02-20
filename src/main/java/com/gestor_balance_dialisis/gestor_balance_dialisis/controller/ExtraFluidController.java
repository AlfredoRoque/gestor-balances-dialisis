package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ExtraFluidRequestDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ExtraFluidResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.ExtraFluidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing extra fluids, including saving new extra fluid records and retrieving existing ones.
 * This controller will handle HTTP requests related to extra fluids for patients, such as urine and ingested fluids.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Extra fluids controller", description = "Service for extra fluids management.")
@RequestMapping("/api/extra-fluids")
public class ExtraFluidController {

    private final ExtraFluidService extraFluidService;

    /**
     * Endpoint to save a new extra fluid record with the provided information.
     *
     * @param extraFluidRequestDto The request body containing the details of the extra fluid record to be saved.
     * @return A ResponseEntity containing the saved extra fluid response and an HTTP status code.
     */
    @Operation(summary = "Save a new extra fluid", description = "Endpoint to save a new extra fluid record with the provided information.")
    @PostMapping("/save")
    public ResponseEntity<ExtraFluidResponseDto> saveVitalSign(@Valid @RequestBody ExtraFluidRequestDto extraFluidRequestDto) {
        return ResponseEntity.created(URI.create("/api/extra-fluids/save")).body(extraFluidService.save(extraFluidRequestDto));
    }

    /**
     * Endpoint to retrieve extra fluid records for the actual date and a specific patient.
     *
     * @param patientId The ID of the patient for whom to retrieve extra fluid records.
     * @return A ResponseEntity containing a list of extra fluid responses and an HTTP status code.
     */
    @Operation(summary = "Get extra fluids for actual date and patient", description = "Endpoint to retrieve extra fluid records for the actual date and a specific patient.")
    @GetMapping("/patients/actual-date/{patientId}")
    public ResponseEntity<List<ExtraFluidResponseDto>> getExtraFluidByActualDateAndPatient(@RequestParam Instant actualDate,
                                                                                           @PathVariable Long patientId) {
        return ResponseEntity.ok(extraFluidService.getExtraFluidByActualDateAndPatient(patientId,actualDate));
    }

    /**
     * Endpoint to retrieve extra fluid records for a specific date range and a specific patient.
     *
     * @param patientId The ID of the patient for whom to retrieve extra fluid records.
     * @param startDate The start date of the date range for which to retrieve extra fluid records.
     * @param endDate   The end date of the date range for which to retrieve extra fluid records (optional).
     * @return A ResponseEntity containing a list of extra fluid responses and an HTTP status code.
     */
    @Operation(summary = "Get extra fluids for actual date and patient", description = "Endpoint to retrieve extra fluid records for the actual date and a specific patient.")
    @GetMapping("/patients/{patientId}/dates")
    public ResponseEntity<List<ExtraFluidResponseDto>> getExtraFluidByDateAndPatient(@PathVariable Long patientId,
                                                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
                                                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam Instant endDate) {
        return ResponseEntity.ok(extraFluidService.getExtraFluidByDateAndPatient(patientId,startDate,endDate));
    }
}

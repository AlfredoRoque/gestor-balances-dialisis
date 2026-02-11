package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PatientRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PatientResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Patient controller", description = "Service for patient management and information.")
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    /**
     * Endpoint to save a new patient.
     *
     * @param patientRequest the patient request containing the patient's information
     * @return ResponseEntity containing the saved patient response
     */
    @Operation(summary = "Save a new patient", description = "Endpoint to save a new patient with the provided information.")
    @PostMapping("/save")
    public ResponseEntity<PatientResponse> saveUser(@Valid @RequestBody PatientRequest patientRequest) {
        return ResponseEntity.created(URI.create("/api/patients/save" + patientRequest.getId())).body(patientService.save(patientRequest));
    }

    /**
     * Endpoint to retrieve patients associated with a specific user ID.
     *
     * @param userId the ID of the user whose patients are to be retrieved
     * @return ResponseEntity containing a list of patient responses associated with the user ID
     */
    @Operation(summary = "Get patients for a user", description = "Endpoint to retrieve patients associated with a specific user ID.")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PatientResponse>> getPatientsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(patientService.findByUserId(userId));
    }
}

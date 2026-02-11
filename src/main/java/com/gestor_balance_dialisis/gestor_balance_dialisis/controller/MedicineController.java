package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.MedicineRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.MedicineResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicine saved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @PostMapping("/save")
    public ResponseEntity<MedicineResponse> saveMedicine(@Valid @RequestBody MedicineRequest medicineRequest) {
        return ResponseEntity.ok(medicineService.save(medicineRequest));
    }

    /**
     * Endpoint to retrieve all medicines available in the system.
     *
     * @return a list of responses containing the information of all medicines
     */
    @Operation(summary = "Get all Medicines", description = "Endpoint to retrieve all medicines available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicines retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }
}

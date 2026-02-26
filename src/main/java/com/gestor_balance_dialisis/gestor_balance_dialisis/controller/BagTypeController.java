package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.BagTypeRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.BagTypeResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.BagTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controller for managing bag types, including saving new bag types and retrieving existing ones.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Bag type controller", description = "Service for bag type management.")
@RequestMapping("/api/bag-types")
public class BagTypeController {

    private final BagTypeService bagTypeService;

    /**
     * Endpoint to save a new bag type with the provided information.
     *
     * @param bagTypeRequest The request body containing the details of the bag type to be saved.
     * @return A ResponseEntity containing the saved bag type response and an HTTP status code.
     */
    @Operation(summary = "Save a new bag type", description = "Endpoint to save a new bag type with the provided information.")
    @PostMapping("/save")
    public ResponseEntity<BagTypeResponse> saveVitalSign(@Valid @RequestBody BagTypeRequest bagTypeRequest) {
        return ResponseEntity.created(URI.create("/api/bag-types/save")).body(bagTypeService.save(bagTypeRequest));
    }

    /**
     * Endpoint to retrieve all bag types available in the system.
     *
     * @return A ResponseEntity containing a list of bag type responses and an HTTP status code.
     */
    @Operation(summary = "Get all bag types", description = "Endpoint to retrieve all bag types available in the system.")
    @GetMapping
    public ResponseEntity<List<BagTypeResponse>> getAllVitalSigns() {
        return ResponseEntity.ok(bagTypeService.getAllBagTypes());
    }
}

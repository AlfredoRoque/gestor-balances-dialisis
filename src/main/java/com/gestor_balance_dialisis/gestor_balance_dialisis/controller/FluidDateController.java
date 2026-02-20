package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.service.FluidDateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing fluid balance dates, providing endpoints to retrieve available dates for fluid balance records.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Fluid balance dates controller", description = "Service for managing fluid balance dates, providing endpoints to retrieve available dates for fluid balance records.")
@RequestMapping("/api/fluid-dates")
public class FluidDateController {

    private final FluidDateService fluidDateService;

    /**
     * Endpoint to retrieve all active fluid dates available in the system for fluid balance records.
     *
     * @return ResponseEntity containing a list of active fluid dates and an HTTP status code.
     */
    @Operation(summary = "Get active fluid dates", description = "Endpoint to retrieve all active fluid dates available in the system for fluid balance records.")
    @GetMapping("/active-dates")
    public ResponseEntity<List<String>> getFluidDates() {
        return ResponseEntity.ok(fluidDateService.getAllFluidDates());
    }
}

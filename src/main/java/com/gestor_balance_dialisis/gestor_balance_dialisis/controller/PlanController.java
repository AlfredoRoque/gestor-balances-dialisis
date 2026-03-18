package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PlanDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.remote.service.PlanRemoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing Plans, including retrieval of Plan information.
 * This controller provides an endpoint to fetch the list of available Plans.
 * It uses the PlanRemoteService to interact with the underlying service layer to retrieve Plan data.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Plan Service", description = "Service for managing Plans, including retrieval of Plan information.")
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanRemoteService planService;

    /**
     * Endpoint to retrieve the list of available Plans.
     * @return ResponseEntity containing a list of PlanDto objects representing the available Plans.
     */
    @GetMapping
    public ResponseEntity<List<PlanDto>> createSubscription(){
        return ResponseEntity.ok(planService.getPlans());
    }
}
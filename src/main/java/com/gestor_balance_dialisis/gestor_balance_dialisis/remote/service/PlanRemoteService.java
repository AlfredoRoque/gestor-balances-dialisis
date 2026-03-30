package com.gestor_balance_dialisis.gestor_balance_dialisis.remote.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PlanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Service responsible for communicating with the remote Plan API to retrieve plan information.
 */
@RequiredArgsConstructor
@Service
public class PlanRemoteService {

    private final WebClient webClient;

    @Value("${plan.api.plans.path}")
    private String GET_PLANS;

    /**
     * Retrieves a list of plans from the remote Plan API.
     * @return A list of PlanDto objects representing the plans retrieved from the remote API.
     */
    public List<PlanDto> getPlans() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_PLANS)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PlanDto>>() {})
                .block();
    }
}

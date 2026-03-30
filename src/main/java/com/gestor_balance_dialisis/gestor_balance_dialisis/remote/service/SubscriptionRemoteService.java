package com.gestor_balance_dialisis.gestor_balance_dialisis.remote.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PaymentSubscriptionResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.SubscriptionDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

/**
 * Service class responsible for communicating with the subscription API to retrieve user subscription information and check for existing subscriptions.
 */
@RequiredArgsConstructor
@Service
public class SubscriptionRemoteService {

    private final WebClient webClient;

    @Value("${subscription.api.user.path}")
    private String GET_USER_SUBSCRIPTION;

    @Value("${subscription.api.exist.path}")
    private String EXIST_SUBSCRIPTION;

    /**
     * Retrieves the subscription information for a given user ID by making a GET request to the subscription API. The user ID is passed as a query parameter in the request. The response is expected to be of type SubscriptionDto, which contains the subscription details for the user.
     * @param userId The ID of the user for whom the subscription information is to be retrieved.
     * @return A SubscriptionDto object containing the subscription details for the specified user. If the request fails or the response is null, it may return null or throw an exception depending on the implementation of the WebClient and error handling strategy.
     */
    public SubscriptionDto getUserSubscription(Long userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_USER_SUBSCRIPTION)
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .bodyToMono(SubscriptionDto.class)
                .block();
    }

    /**
     * Checks if there is an existing subscription for the current user by making a GET request to the subscription API. The response is expected to be of type PaymentSubscriptionResponseDto, which contains information about the existence of a subscription and its details if it exists.
     * @return A PaymentSubscriptionResponseDto object containing information about the existence of a subscription for the current user. If the request fails or the response is null, it may return null or throw an exception depending on the implementation of the WebClient and error handling strategy.
     */
    public PaymentSubscriptionResponseDto existSubscription() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(EXIST_SUBSCRIPTION)
                        .build())
                .retrieve()
                .bodyToMono(PaymentSubscriptionResponseDto.class)
                .block();
    }
}

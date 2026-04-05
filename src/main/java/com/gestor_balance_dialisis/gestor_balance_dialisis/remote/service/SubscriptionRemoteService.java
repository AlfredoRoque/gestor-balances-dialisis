package com.gestor_balance_dialisis.gestor_balance_dialisis.remote.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PaymentSubscriptionResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service class responsible for communicating with the subscription API to retrieve
 * user subscription information and check for existing subscriptions.
 */
@RequiredArgsConstructor
@Service
public class SubscriptionRemoteService {

    private final RestClient restClient;

    @Value("${subscription.api.user.path}")
    private String GET_USER_SUBSCRIPTION;

    @Value("${subscription.api.exist.path}")
    private String EXIST_SUBSCRIPTION;

    /**
     * Retrieves the subscription information for a given user ID.
     * @param userId The ID of the user for whom the subscription information is to be retrieved.
     * @return A SubscriptionDto object containing the subscription details.
     */
    public SubscriptionDto getUserSubscription(Long userId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_USER_SUBSCRIPTION)
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .body(SubscriptionDto.class);
    }

    /**
     * Checks if there is an existing subscription for the current user.
     * @return A PaymentSubscriptionResponseDto containing information about the subscription.
     */
    public PaymentSubscriptionResponseDto existSubscription() {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(EXIST_SUBSCRIPTION)
                        .build())
                .retrieve()
                .body(PaymentSubscriptionResponseDto.class);
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.remote.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PaymentSubscriptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service for handling remote calls to the payment API for subscription management, including creating, canceling, and changing subscriptions, as well as updating card information.
 */
@RequiredArgsConstructor
@Service
public class PaymentRemoteService {

    private final WebClient webClient;

    @Value("${subscription.api.create.path}")
    private String CREATE_PAYMENT;

    @Value("${subscription.api.cancel.path}")
    private String CANCEL_PAYMENT;

    @Value("${subscription.api.change.path}")
    private String CHANGE_SUBSCRIPTION_PLAN;

    @Value("${subscription.api.cards.path}")
    private String CHANGE_CARDS;

    /**
     * Create a new subscription for the user based on the provided price ID. This method makes a POST request to the payment API and returns the response containing subscription details.
     * @param priceId The ID of the price plan for the subscription to be created.
     * @return A PaymentSubscriptionResponseDto containing details about the created subscription, such as status, next billing date, and any relevant messages.
     */
    public PaymentSubscriptionResponseDto createSubscription(String priceId) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(CREATE_PAYMENT)
                        .queryParam("priceId", priceId)
                        .build())
                .retrieve()
                .bodyToMono(PaymentSubscriptionResponseDto.class)
                .block();
    }

    /**
     * Cancel the user's current subscription. This method sends a POST request to the payment API to cancel the subscription and returns the response indicating the result of the cancellation, including any relevant messages or status updates.
     * @return A PaymentSubscriptionResponseDto containing details about the cancellation, such as status and any relevant messages.
     */
    public PaymentSubscriptionResponseDto cancelSubscription() {
        return  webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(CANCEL_PAYMENT)
                        .build())
                .retrieve()
                .bodyToMono(PaymentSubscriptionResponseDto.class)
                .block();
    }

    /**
     * Change the user's current subscription to a new price plan. This method makes a POST request to the payment API with the new price ID and returns the response containing details about the updated subscription, including status, next billing date, and any relevant messages.
     * @param newPriceId The ID of the new price plan to which the subscription should be changed.
     * @return A PaymentSubscriptionResponseDto containing details about the updated subscription, such as status, next billing date, and any relevant messages.
     */
    public PaymentSubscriptionResponseDto changeSubscription(String newPriceId) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(CHANGE_SUBSCRIPTION_PLAN)
                        .queryParam("newPriceId", newPriceId)
                        .build())
                .retrieve()
                .bodyToMono(PaymentSubscriptionResponseDto.class)
                .block();
    }

    /**
     * Update the user's card information for the subscription. This method sends a POST request to the payment API to change the card details associated with the subscription and returns the response indicating the result of the update, including any relevant messages or status updates.
     * @return A PaymentSubscriptionResponseDto containing details about the card update, such as status and any relevant messages.
     */
    public PaymentSubscriptionResponseDto changeCards() {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(CHANGE_CARDS)
                        .build())
                .retrieve()
                .bodyToMono(PaymentSubscriptionResponseDto.class)
                .block();
    }
}

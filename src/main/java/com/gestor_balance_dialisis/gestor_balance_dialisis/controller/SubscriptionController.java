package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PaymentSubscriptionResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.SubscriptionDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing Subscription-related operations, including retrieval of Subscription information and checking for existing subscriptions.
 * This controller provides endpoints for clients to interact with the Subscription service, allowing them to check if a subscription exists and to retrieve subscription details. The controller is annotated with Swagger/OpenAPI annotations for API documentation purposes.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Subscription Service", description = "Service for managing Subscription, including retrieval of Subscription information.")
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * Endpoint to check if the user has an existing subscription. This endpoint returns a PaymentSubscriptionResponseDto indicating whether the user has an active subscription and any relevant details about the subscription status.
     * @return ResponseEntity containing a PaymentSubscriptionResponseDto with the subscription status of the user.
     */
    @GetMapping("/users/exist-subscription")
    public ResponseEntity<PaymentSubscriptionResponseDto> existSubscription() {
        return ResponseEntity.ok(subscriptionService.existSubscription());
    }

    /**
     * Endpoint to retrieve the subscription details for the user. This endpoint returns a SubscriptionDto containing information about the user's subscription, such as the subscription type, status, and any relevant dates or details associated with the subscription.
     * @return ResponseEntity containing a SubscriptionDto with the subscription details of the user.
     */
    @GetMapping("/users/subscription")
    public ResponseEntity<SubscriptionDto> getSubscription() {
        return ResponseEntity.ok(subscriptionService.getSubscription(null));
    }
}

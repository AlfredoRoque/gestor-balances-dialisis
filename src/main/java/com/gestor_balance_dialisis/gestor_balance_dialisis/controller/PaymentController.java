package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PaymentSubscriptionResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.remote.service.PaymentRemoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing payment-related operations, including creating subscriptions, canceling subscriptions, changing subscription plans, and changing payment cards. This controller interacts with the PaymentRemoteService to perform the necessary business logic and returns appropriate responses to the client.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Payment Service", description = "Service for managing payments, including retrieval of payment information and processing of payments.")
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRemoteService paymentRemoteService;

    /**
     * Create a new subscription for the user based on the provided price ID. This endpoint allows users to subscribe to a specific plan or service by providing the corresponding price ID. The response includes details about the created subscription, such as the subscription ID, status, and any relevant information related to the subscription.
     * @param priceId The ID of the price or plan that the user wants to subscribe to. This parameter is required to identify the specific subscription plan that the user intends to subscribe to.
     * @return A ResponseEntity containing a PaymentSubscriptionResponseDto with details about the created subscription, including the subscription ID, status, and any relevant information related to the subscription. The response is returned with an HTTP status of 200 OK if the subscription is successfully created.
     */
    @PostMapping
    public ResponseEntity<PaymentSubscriptionResponseDto> createSubscription(@RequestParam String priceId) {
        return ResponseEntity.ok(paymentRemoteService.createSubscription(priceId));
    }

    /**
     * Cancel the user's current subscription. This endpoint allows users to cancel their existing subscription, which may involve stopping recurring payments and updating the subscription status accordingly. The response includes details about the canceled subscription, such as the subscription ID, status, and any relevant information related to the cancellation.
     * @return A ResponseEntity containing a PaymentSubscriptionResponseDto with details about the canceled subscription, including the subscription ID, status, and any relevant information related to the cancellation. The response is returned with an HTTP status of 200 OK if the subscription is successfully canceled.
     */
    @PostMapping("/cancel")
    public ResponseEntity<PaymentSubscriptionResponseDto> cancelSubscription() {
        return ResponseEntity.ok(paymentRemoteService.cancelSubscription());
    }

    /**
     * Change the user's current subscription plan to a new plan based on the provided price ID. This endpoint allows users to switch their existing subscription to a different plan by providing the corresponding price ID for the new plan. The response includes details about the updated subscription, such as the subscription ID, status, and any relevant information related to the plan change.
     * @param priceId The ID of the new price or plan that the user wants to switch to. This parameter is required to identify the specific subscription plan that the user intends to switch to. The provided price ID should correspond to a valid subscription plan available in the system.
     * @return A ResponseEntity containing a PaymentSubscriptionResponseDto with details about the updated subscription, including the subscription ID, status, and any relevant information related to the plan change. The response is returned with an HTTP status of 200 OK if the subscription is successfully updated to the new plan.
     */
    @PostMapping("/change-plan")
    public ResponseEntity<PaymentSubscriptionResponseDto> changeSubscription(@RequestParam String priceId) {
        return ResponseEntity.ok(paymentRemoteService.changeSubscription(priceId));
    }

    /**
     * Change the user's current payment card information. This endpoint allows users to update their payment card details, which may involve providing new card information or selecting a different card for future payments. The response includes details about the updated subscription, such as the subscription ID, status, and any relevant information related to the card change.
     * @return A ResponseEntity containing a PaymentSubscriptionResponseDto with details about the updated subscription, including the subscription ID, status, and any relevant information related to the card change. The response is returned with an HTTP status of 200 OK if the payment card information is successfully updated.
     */
    @PostMapping("/change-cards")
    public ResponseEntity<PaymentSubscriptionResponseDto> changeCards() {
        return ResponseEntity.ok(paymentRemoteService.changeCards());
    }
}

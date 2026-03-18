package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.PaymentSubscriptionResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.SubscriptionDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.remote.service.SubscriptionRemoteService;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service responsible for managing user subscriptions, including checking if a subscription exists and retrieving the subscription details for a user. It interacts with a remote service to perform these operations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final SubscriptionRemoteService  subscriptionRemoteService;

    /**
     * Check if the user has an active subscription, returns a response indicating the subscription status and details if applicable.
     * @return PaymentSubscriptionResponseDto indicating whether the user has an active subscription and the subscription details if applicable.
     */
    public PaymentSubscriptionResponseDto existSubscription() {
        return subscriptionRemoteService.existSubscription();
    }

    /**
     * Retrieve the subscription details for a user, returns the subscription details for the specified user ID or the current user if no ID is provided.
     * @param userId The ID of the user for whom to retrieve the subscription details. If null, the subscription details for the current user will be retrieved.
     * @return SubscriptionDto containing the subscription details for the specified user ID or the current user if no ID is provided.
     */
    public SubscriptionDto getSubscription(Long userId) {
        return subscriptionRemoteService.getUserSubscription(Objects.nonNull(userId)?userId: SecurityUtils.getUserId());
    }
}

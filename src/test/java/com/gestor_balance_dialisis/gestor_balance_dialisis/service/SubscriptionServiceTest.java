package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.SubscriptionDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.remote.service.SubscriptionRemoteService;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SubscriptionService class, which handles subscription-related operations by delegating to the SubscriptionRemoteService. These tests verify that the service correctly interacts with the remote service and handles user ID retrieval from the security context when necessary.
 */
@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock private SubscriptionRemoteService subscriptionRemoteService;

    @InjectMocks private SubscriptionService subscriptionService;

    /**
     * Test that getSubscription calls the remote service with the provided user ID.
     * Verifies that when a non-null user ID is passed, the service delegates directly
     * to the remote service and returns the subscription DTO with the correct user ID.
     */
    @Test
    void getSubscription_withUserId_callsRemoteWithGivenId() {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setUserId(5L);
        when(subscriptionRemoteService.getUserSubscription(5L)).thenReturn(dto);

        SubscriptionDto result = subscriptionService.getSubscription(5L);

        assertThat(result.getUserId()).isEqualTo(5L);
        verify(subscriptionRemoteService).getUserSubscription(5L);
    }

    /**
     * Test that getSubscription uses the current user's ID when a null ID is provided.
     * Verifies that when null is passed, the service retrieves the user ID from the security context
     * and delegates to the remote service with that ID.
     */
    @Test
    void getSubscription_withNullId_usesCurrentUserId() {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setUserId(3L);

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserId).thenReturn(3L);
            when(subscriptionRemoteService.getUserSubscription(3L)).thenReturn(dto);

            SubscriptionDto result = subscriptionService.getSubscription(null);

            assertThat(result.getUserId()).isEqualTo(3L);
            verify(subscriptionRemoteService).getUserSubscription(3L);
        }
    }

    /**
     * Test that existSubscription delegates the call to the remote subscription service.
     * Verifies that the method correctly forwards the request without additional processing.
     */
    @Test
    void existSubscription_delegatesToRemoteService() {
        when(subscriptionRemoteService.existSubscription()).thenReturn(null);

        subscriptionService.existSubscription();

        verify(subscriptionRemoteService).existSubscription();
    }
}

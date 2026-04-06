package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.NotificationResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.UserRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the NotificationService class, covering various scenarios related to user notifications and balance cleanup.
 * The tests verify the behavior of the notificationCleanBalancesForUser method under different conditions, including user existence, timing of notifications, and cleanup triggers based on the user's last clean date.
 * Each test case sets up the necessary mocks for utility methods and repository interactions to isolate the service logic and ensure accurate verification of outcomes and side effects.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private FluidBalanceService fluidBalanceService;
    @Mock private UserRepository userRepository;

    @InjectMocks private NotificationService notificationService;

    /**
     * Helper method to set up common mocks for SecurityUtils and Utility static methods used in the notification service.
     * @param util: MockedStatic instance for Utility class to mock static methods.
     * @param su: MockedStatic instance for SecurityUtils class to mock static methods.
     * @param baseDate: The base date to be returned by the first call to Utility.startDay, representing the current day for the test scenario.
     * @param lastDay: The last day of the current month to be returned by Utility.getLastDayOfMonth.
     * @param lastDayPrevMonth: The last day of the previous month to be returned by Utility.getLastDayOfPreviousMonth.
     * @param minusDaysResult: The result to be returned by Utility.minusDays when calculating the notification date.
     * @param dateForNotificationReturn: The date to be returned by the second call to Utility.startDay, representing the calculated date for sending notifications based on the current day minus the notification window.
     */
    // Helper: sets up common mocks.
    // startDay is called TWICE by the service:
    //   1st → for actualDay = startDay(now) + 1 min
    //   2nd → for dateForNotification = startDay(minusDays(5, lastDay))
    // We use consecutive returns: first=baseDate, second=dateForNotificationReturn
    private void mockUtilityAndSecurity(
            MockedStatic<Utility> util,
            MockedStatic<SecurityUtils> su,
            Instant baseDate,
            Instant lastDay,
            Instant lastDayPrevMonth,
            Instant minusDaysResult,
            Instant dateForNotificationReturn) {

        su.when(SecurityUtils::getUserId).thenReturn(1L);
        su.when(SecurityUtils::getUserZone).thenReturn(ZoneOffset.UTC);

        // startDay called twice – first returns baseDate, second returns dateForNotificationReturn
        util.when(() -> Utility.startDay(any()))
                .thenReturn(baseDate)
                .thenReturn(dateForNotificationReturn);

        util.when(Utility::getLastDayOfMonth).thenReturn(lastDay);
        util.when(Utility::getLastDayOfPreviousMonth).thenReturn(lastDayPrevMonth);
        util.when(() -> Utility.minusDays(anyInt(), any())).thenReturn(minusDaysResult);
    }

    // ─── Scenario: early in month, user not found → no notification, no clean ─

    /**
     * Test that no notification is returned and no cleanup occurs when the user is not found.
     * Verifies that when the actual day is early in the month (before the notification window)
     * and the user does not exist in the repository, the service returns a false notification
     * and never triggers the balance cleanup process.
     */
    @Test
    void notificationCleanBalancesForUser_noNotification_userNotFound_returnsFalse() {
        // baseDate = June 10 → actualDay = June 10 00:01
        // dateForNotification = June 26 (after actualDay → no notification)
        // actualDay after lastDayPrevMonth (May 31) → enters else if
        // userRepository returns empty → no clean executed
        Instant baseDate        = Instant.parse("2024-06-10T00:00:00Z");
        Instant lastDay         = Instant.parse("2024-06-30T23:59:59Z");
        Instant lastDayPrevMonth= Instant.parse("2024-05-31T23:59:59Z");
        Instant minusDaysResult = Instant.parse("2024-06-25T12:00:00Z");
        Instant dateForNotif    = Instant.parse("2024-06-26T00:00:00Z");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {

            mockUtilityAndSecurity(util, su, baseDate, lastDay, lastDayPrevMonth, minusDaysResult, dateForNotif);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            NotificationResponseDto result = notificationService.notificationCleanBalancesForUser();

            assertThat(result.isNotification()).isFalse();
            verify(fluidBalanceService, never()).cleanFluidBalanceForPatientAndUser(any(), any());
        }
    }

    // ─── Scenario: last 5 days of month → notification returned ──────────────

    /**
     * Test that a notification is returned when the current date is within the last 5 days of the month.
     * Verifies that when the actual day falls between the notification date and the last day of the month,
     * the service returns a response with notification set to true and a non-null message.
     */
    @Test
    void notificationCleanBalancesForUser_insideLastFiveDays_returnsNotification() {
        // baseDate = June 27 → actualDay = June 27 00:01
        // dateForNotification = June 26 (before actualDay)
        // lastDay = June 30
        // → actualDay.isAfter(dateForNotif) && actualDay.isBefore(lastDay) → notification!
        Instant baseDate        = Instant.parse("2024-06-27T00:00:00Z");
        Instant lastDay         = Instant.parse("2024-06-30T23:59:59Z");
        Instant lastDayPrevMonth= Instant.parse("2024-05-31T23:59:59Z");
        Instant minusDaysResult = Instant.parse("2024-06-25T12:00:00Z");
        Instant dateForNotif    = Instant.parse("2024-06-26T00:00:00Z");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {

            mockUtilityAndSecurity(util, su, baseDate, lastDay, lastDayPrevMonth, minusDaysResult, dateForNotif);

            NotificationResponseDto result = notificationService.notificationCleanBalancesForUser();

            assertThat(result.isNotification()).isTrue();
            assertThat(result.getMessage()).isNotNull();
        }
    }

    // ─── Scenario: early in month, not yet cleaned → triggers clean ───────────

    /**
     * Test that cleanup is triggered when the user's last clean date is before the end of the previous month.
     * Verifies that when the actual day is after the last day of the previous month and the user
     * has not been cleaned in the current period, the service triggers the balance cleanup process
     * and updates the user's last clean date.
     */
    @Test
    void notificationCleanBalancesForUser_afterLastDayPrevMonth_triggersClean_ifNotAlreadyCleaned() {
        // baseDate = July 05 → actualDay = July 05 00:01
        // dateForNotification = July 27 (after actualDay → no notification)
        // lastDayPrevMonth = June 30
        // actualDay.isAfter(lastDayPrevMonth) → YES → enters else if
        // user.lastCleanDate = June 15 (before lastDayPrevMonth) → alreadyCleaned = false → CLEAN!
        Instant baseDate        = Instant.parse("2024-07-05T00:00:00Z");
        Instant lastDay         = Instant.parse("2024-07-31T23:59:59Z");
        Instant lastDayPrevMonth= Instant.parse("2024-06-30T23:59:59Z");
        Instant minusDaysResult = Instant.parse("2024-07-26T12:00:00Z");
        Instant dateForNotif    = Instant.parse("2024-07-27T00:00:00Z");

        User user = new User();
        user.setId(1L);
        user.setLastCleanDate(Instant.parse("2024-06-15T00:00:00Z")); // before lastDayPrevMonth

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {

            mockUtilityAndSecurity(util, su, baseDate, lastDay, lastDayPrevMonth, minusDaysResult, dateForNotif);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            doNothing().when(fluidBalanceService).cleanFluidBalanceForPatientAndUser(eq(1L), any());
            when(userRepository.save(any(User.class))).thenReturn(user);

            NotificationResponseDto result = notificationService.notificationCleanBalancesForUser();

            assertThat(result.isNotification()).isFalse();
            verify(fluidBalanceService).cleanFluidBalanceForPatientAndUser(eq(1L), any());
            verify(userRepository).save(any(User.class));
        }
    }

    // ─── Scenario: early in month, already cleaned → skips clean ─────────────

    /**
     * Test that cleanup is skipped when the user's last clean date is after the end of the previous month.
     * Verifies that when the user has already been cleaned in the current period,
     * the service does not trigger the balance cleanup process and returns no notification.
     */
    @Test
    void notificationCleanBalancesForUser_afterLastDayPrevMonth_alreadyCleaned_skipsClean() {
        // Same as previous but user.lastCleanDate = July 01 (after lastDayPrevMonth June 30)
        // → alreadyCleaned = true → skip
        Instant baseDate        = Instant.parse("2024-07-05T00:00:00Z");
        Instant lastDay         = Instant.parse("2024-07-31T23:59:59Z");
        Instant lastDayPrevMonth= Instant.parse("2024-06-30T23:59:59Z");
        Instant minusDaysResult = Instant.parse("2024-07-26T12:00:00Z");
        Instant dateForNotif    = Instant.parse("2024-07-27T00:00:00Z");

        User user = new User();
        user.setId(1L);
        user.setLastCleanDate(Instant.parse("2024-07-01T00:00:00Z")); // after lastDayPrevMonth

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class);
             MockedStatic<Utility> util = mockStatic(Utility.class)) {

            mockUtilityAndSecurity(util, su, baseDate, lastDay, lastDayPrevMonth, minusDaysResult, dateForNotif);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            NotificationResponseDto result = notificationService.notificationCleanBalancesForUser();

            assertThat(result.isNotification()).isFalse();
            verify(fluidBalanceService, never()).cleanFluidBalanceForPatientAndUser(any(), any());
        }
    }
}


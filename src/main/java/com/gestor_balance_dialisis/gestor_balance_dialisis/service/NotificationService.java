package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.NotificationResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Service responsible for handling notifications related to cleaning balances. It checks if the user should receive a notification to clean balances based on the current date and the last day of the month. If the period for backup expires, it also triggers the cleaning of balances for the user.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final FluidBalanceService fluidBalanceService;

    /**
     * Check if the user should receive a notification to clean balances based on the current date and the last day of the month.
     * @return NotificationResponseDto indicating whether the user should receive a notification and the message to be displayed if applicable.
     */
    public NotificationResponseDto notificationCleanBalancesForUser(){
        log.info("Start notificationCleanBalancesForUser for userId: {}", SecurityUtils.getUserId());
        Instant actualDay = Utility.startDay(Instant.now()).plus(1, ChronoUnit.MINUTES);
        Instant lastDay = Utility.getLastDayOfMonth();
        Instant dateForNotification = Utility.startDay(Utility.minusDays(Constants.DAYS_BEFORE_CLEAN_BALANCES, lastDay));
        Instant lastDayOfBeforeMonth = Utility.getLastDayOfMonth().atZone(SecurityUtils.getUserZone()).minusMonths(1).toInstant();

        if(actualDay.isAfter(dateForNotification) && actualDay.isBefore(lastDay)){
            return new NotificationResponseDto(true,
                    String.format(Constants.MESSAGE_NOTIFICATION_FOR_CLEAN_HISTORY,
                            lastDayOfBeforeMonth.atZone(SecurityUtils.getUserZone()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }else if(actualDay.isAfter(lastDay)){
            //clean balances if period for backup expire
            fluidBalanceService.cleanFluidBalanceForPatientAndUser(SecurityUtils.getUserId(),actualDay);
        }
        return new NotificationResponseDto(false);
    }
}

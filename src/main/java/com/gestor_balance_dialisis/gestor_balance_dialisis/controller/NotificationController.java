package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.NotificationResponseDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling HTTP requests related to notifications in the application. It provides endpoints for retrieving warning notifications for users to clean their fluid balance history, informing them about the importance of generating backups of their previous balances before a specified date to avoid permanent loss.
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "Notification controller", description = "Service for Notification management.")
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Endpoint to retrieve a warning notification for users to clean their fluid balance history, informing them about the importance of generating backups of their previous balances before a specified date to avoid permanent loss.
     * @return NotificationResponseDto containing the warning notification message for users to clean their fluid balance history and generate backups of their previous balances before a specified date to avoid permanent loss.
     */
    @Operation(summary = "Get warning notification for clean balances",
            description = "Endpoint to retrieve a warning notification for users to clean their fluid balance history, " +
                    "informing them about the importance of generating backups of their previous balances before a specified date to avoid permanent loss.")
    @GetMapping("/users/balances/clean-history")
    public NotificationResponseDto notificationCleanBalancesForUser(){
        return notificationService.notificationCleanBalancesForUser();
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for notification response, indicating whether a notification should be shown to the user and providing an optional message.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Notification message", example = "Create backup for your information")
    private String message;

    @Schema(description = "Indicates if notification exist", example = "true")
    private boolean notification;

    /**
     * Constructor for NotificationResponseDto with only the notification flag.
     * @param isNotification Indicates whether the user should receive a notification.
     */
    public NotificationResponseDto(boolean isNotification) {
        this.notification = isNotification;
    }

    /**
     * Constructor for NotificationResponseDto with both the notification flag and a message.
     * @param isNotification Indicates whether the user should receive a notification.
     * @param message The message to be displayed to the user if a notification is needed.
     */
    public NotificationResponseDto(boolean isNotification, String message) {
        this.notification = isNotification;
        this.message = message;
    }
}

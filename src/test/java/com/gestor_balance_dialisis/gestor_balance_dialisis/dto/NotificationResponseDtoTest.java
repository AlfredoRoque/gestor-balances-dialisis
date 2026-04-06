package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the NotificationResponseDto class, covering all constructors and field mappings.
 * This test class verifies that the DTO correctly initializes its fields based on the provided arguments and that the default values are set as expected when using the no-args constructor.
 */
class NotificationResponseDtoTest {

    /**
     * Test that the single-argument constructor sets notification to false with a null message.
     * Verifies the DTO state when only the notification flag is provided as false.
     */
    @Test
    void constructor_notificationOnly_false() {
        NotificationResponseDto dto = new NotificationResponseDto(false);
        assertThat(dto.isNotification()).isFalse();
        assertThat(dto.getMessage()).isNull();
    }

    /**
     * Test that the single-argument constructor sets notification to true.
     * Verifies the DTO state when the notification flag is provided as true.
     */
    @Test
    void constructor_notificationOnly_true() {
        NotificationResponseDto dto = new NotificationResponseDto(true);
        assertThat(dto.isNotification()).isTrue();
    }

    /**
     * Test that the two-argument constructor correctly sets both notification and message fields.
     * Verifies that the DTO holds the provided boolean flag and message string.
     */
    @Test
    void constructor_withMessage_setsFields() {
        NotificationResponseDto dto = new NotificationResponseDto(true, "Backup your data");
        assertThat(dto.isNotification()).isTrue();
        assertThat(dto.getMessage()).isEqualTo("Backup your data");
    }

    /**
     * Test that the no-args constructor initializes notification to false and message to null.
     * Verifies the default state of the DTO when created without arguments.
     */
    @Test
    void noArgs_constructor_defaults() {
        NotificationResponseDto dto = new NotificationResponseDto();
        assertThat(dto.isNotification()).isFalse();
        assertThat(dto.getMessage()).isNull();
    }
}

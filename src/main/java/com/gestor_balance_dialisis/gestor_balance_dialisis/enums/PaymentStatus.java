package com.gestor_balance_dialisis.gestor_balance_dialisis.enums;

import lombok.Getter;

/**
 * Enum representing the possible payment statuses for a transaction.
 * - PAGADO: Indicates that the payment has been successfully completed.
 * - CANCELADO: Indicates that the payment has been canceled.
 * - PENDIENTE_CANCELAR: Indicates that the payment is pending cancellation.
 * - FALLIDO: Indicates that the payment has failed.
 */
@Getter
public enum PaymentStatus {
    PAGADO,CANCELADO, PENDIENTE_CANCELAR, FALLIDO
}

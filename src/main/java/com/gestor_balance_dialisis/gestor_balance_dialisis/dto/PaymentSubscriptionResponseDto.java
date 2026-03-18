package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for payment subscription response, containing the payment URL, message response, and a flag indicating if a subscription exists.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSubscriptionResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Payment url", example = "https://checkout.stripe.com/pay/fgf...")
    private String paymentUrl;

    @Schema(description = "Message response", example = "Subscription cancelled successfully")
    private String message;

    @Schema(description = "Exist subscription", example = "true")
    private boolean existSubscription;
}

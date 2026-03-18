package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Data Transfer Object for Subscription entity, used to transfer subscription data between different layers of the application.
 * It includes fields for subscription details such as id, stripe subscription id, user id, customer id, price id, status, start and end period dates, and the associated plan.
 * This class implements Serializable to allow subscription data to be easily serialized and deserialized when transferring over the network or storing in a session.
 * The class is annotated with Lombok annotations to generate boilerplate code such as getters, setters, constructors, and toString method, reducing the amount of code needed to manage subscription data.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Id for subscription", example = "123")
    private Long id;

    @Schema(description = "Id stripe for subscription", example = "sub_123")
    private String stripeSubscriptionId ;

    @Schema(description = "User id", example = "123")
    private Long userId;

    @Schema(description = "Stripe customer id", example = "cus_123")
    private String customerId;

    @Schema(description = "Stripe price id", example = "price_123")
    private String priceId;

    @Schema(description = "Subscription status", example = "PAGADO")
    private PaymentStatus status;

    @Schema(description = "Start period subscription", example = "2024-01-01T00:00:00Z")
    private Instant startPeriodDate;

    @Schema(description = "End period subscription", example = "2024-01-31T23:59:59Z")
    private Instant endPeriodDate;

    @Schema(description = "Plan for subscription", example = "Basic Plan")
    private PlanDto plan;
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.config;

import com.gestor_balance_dialisis.gestor_balance_dialisis.security.JwtPropagationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for setting up the WebClient used to communicate with external services, specifically the subscription API.
 * It includes the base URL for the subscription API and applies a JWT propagation filter to ensure that authentication tokens are included in outgoing requests.
 */
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

    @Value("${subscription.api.host.uri}")
    private String HOST_PAYMENT;

    private final JwtPropagationFilter jwtPropagationFilter;

    /**
     * Creates and configures a WebClient bean with the base URL for the subscription API and applies the JWT propagation filter to ensure that authentication tokens are included in outgoing requests.
     * @return a configured WebClient instance ready to be used for making HTTP requests to the subscription API.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(HOST_PAYMENT)
                .filter(jwtPropagationFilter.jwtFilter())
                .build();
    }

}

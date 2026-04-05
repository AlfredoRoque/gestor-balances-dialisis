package com.gestor_balance_dialisis.gestor_balance_dialisis.config;

import com.gestor_balance_dialisis.gestor_balance_dialisis.security.JwtPropagationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration class for setting up the RestClient used to communicate with external services.
 * Applies a JWT propagation interceptor to ensure authentication tokens are included in outgoing requests.
 */
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

    @Value("${subscription.api.host.uri}")
    private String HOST_PAYMENT;

    private final JwtPropagationFilter jwtPropagationFilter;

    /**
     * Creates and configures a RestClient bean with the base URL for the subscription API
     * and applies the JWT propagation interceptor.
     * @return a configured RestClient instance ready to be used for making HTTP requests.
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(HOST_PAYMENT)
                .requestInterceptor(jwtPropagationFilter)
                .build();
    }
}

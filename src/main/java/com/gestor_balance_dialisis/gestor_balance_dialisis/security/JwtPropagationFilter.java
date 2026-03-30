package com.gestor_balance_dialisis.gestor_balance_dialisis.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserSessionModel;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

/**
 * This filter intercepts outgoing WebClient requests and adds the JWT token and user session information to the headers, allowing for seamless authentication propagation across microservices.
 * It checks for a "NO_AUTH" attribute to determine if authentication should be skipped for certain requests, ensuring flexibility in handling both authenticated and unauthenticated requests as needed.
 */
@Component
@RequiredArgsConstructor
public class JwtPropagationFilter {

    private final ObjectMapper objectMapper;

    /**
     * Creates an ExchangeFilterFunction that adds the JWT token and user session information to the headers of outgoing WebClient requests, unless the "NO_AUTH" attribute is set to true.
     * @return An ExchangeFilterFunction that can be added to a WebClient builder to enable JWT propagation for outgoing requests.
     */
    public ExchangeFilterFunction jwtFilter() {
        return (request, next) -> {

            Boolean noAuth = request.attribute(Constants.NO_AUTH)
                    .map(Boolean.class::cast)
                    .orElse(false);

            ClientRequest.Builder builder = ClientRequest.from(request);

            if (!noAuth) {

                String token = SecurityUtils.getToken();
                UserSessionModel sessionUser = SecurityUtils.getUserSession();

                if (token != null) {
                    builder.header("Authorization", token);
                }

                if (sessionUser != null) {
                    try {
                        String sessionJson = objectMapper.writeValueAsString(sessionUser);
                        builder.header("X-SESSION-USER", sessionJson);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return next.exchange(builder.build());
        };
    }
}
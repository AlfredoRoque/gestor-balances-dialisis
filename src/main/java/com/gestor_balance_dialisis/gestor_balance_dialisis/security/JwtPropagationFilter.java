package com.gestor_balance_dialisis.gestor_balance_dialisis.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserSessionModel;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Interceptor that adds the JWT token and user session information to the headers
 * of outgoing RestClient requests, enabling seamless authentication propagation across microservices.
 */
@Component
@RequiredArgsConstructor
public class JwtPropagationFilter implements ClientHttpRequestInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body,
                                        @NonNull ClientHttpRequestExecution execution) throws IOException {

        String token = SecurityUtils.getToken();
        UserSessionModel sessionUser = SecurityUtils.getUserSession();

        if (token != null) {
            request.getHeaders().set("Authorization", token);
        }

        if (sessionUser != null) {
            try {
                String sessionJson = objectMapper.writeValueAsString(sessionUser);
                request.getHeaders().set("X-SESSION-USER", sessionJson);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return execution.execute(request, body);
    }
}
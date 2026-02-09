package com.gestor_balance_dialisis.gestor_balance_dialisis.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtFilter is a custom filter that intercepts incoming HTTP requests to validate the JWT token present in the "Authorization" header.
 * If a valid token is found, it extracts the username and sets the authentication context for the request.
 */
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * Intercepts incoming HTTP requests to validate the JWT token present in the "Authorization" header.
     * If a valid token is found, it extracts the username and sets the authentication context for the request.
     *
     * @param request  The incoming HTTP request containing the JWT token in the "Authorization" header.
     * @param response The HTTP response to be sent back to the client.
     * @param chain    The filter chain to pass the request and response to the next filter in the chain.
     * @throws ServletException If an error occurs during the filtering process.
     * @throws IOException      If an I/O error occurs during the filtering process.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = jwtUtil.extractUsername(token);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, List.of());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}

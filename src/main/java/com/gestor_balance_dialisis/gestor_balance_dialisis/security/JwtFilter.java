package com.gestor_balance_dialisis.gestor_balance_dialisis.security;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserSessionModel;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.UserRol;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.PatientRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.UserRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * JwtFilter is a custom filter that intercepts incoming HTTP requests to validate the JWT token present in the "Authorization" header.
 * If a valid token is found, it extracts the username and sets the authentication context for the request.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

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

        try {
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                UserRol userRol = UserRol.valueOf(jwtUtil.extractClaim(token, claims -> claims.get("role", String.class)));

                // validate the token and extract username and zone
                UserSessionModel sessionModel = new UserSessionModel(jwtUtil.extractUsername(token),
                        jwtUtil.extractClaim(token, claims -> claims.get("zone", String.class)),
                        jwtUtil.extractClaim(token, claims -> claims.get("userId", Integer.class)),
                        jwtUtil.extractClaim(token, claims -> claims.get("version", Integer.class)),
                        jwtUtil.extractClaim(token, claims -> claims.get("email", String.class)),
                        userRol);

                if(sessionModel.getRole().equals(UserRol.ADMIN)) {
                    Optional<User> user = userRepository.findByUsername(sessionModel.getUsername());
                    user.orElseThrow(() -> new BalanceGlobalException(Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND.value()));
                    if (sessionModel.getTokenVersion() != user.get().getTokenVersion().intValue()) {
                        throw new BalanceGlobalException(Constants.INVALID_TOKEN, HttpStatus.UNAUTHORIZED.value());
                    }
                }else{
                    Optional<Patient> patient = patientRepository.findByName(sessionModel.getUsername());
                    patient.orElseThrow(() -> new BalanceGlobalException(Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND.value()));
                    if (sessionModel.getTokenVersion() != patient.get().getTokenVersion().intValue()) {
                        throw new BalanceGlobalException(Constants.INVALID_TOKEN, HttpStatus.UNAUTHORIZED.value());
                    }
                }

                if (sessionModel.getUsername() != null && !sessionModel.getUsername().isEmpty()) {
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(sessionModel.getRole().name()));

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(sessionModel, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("JWT Token validated for user: {}", sessionModel.getUsername());
                } else {
                    log.warn("failed to extract username from token");
                }
            }
        } catch (JwtException e) {
            log.error("Validate token error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("Token unexpected error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}

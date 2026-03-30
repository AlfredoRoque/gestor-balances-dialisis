package com.gestor_balance_dialisis.gestor_balance_dialisis.config;

import com.gestor_balance_dialisis.gestor_balance_dialisis.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for setting up Spring Security in the application.
 * This class defines the security filter chain, password encoder, and JWT filter for authentication and authorization.
 * It also specifies public paths that do not require authentication and configures CORS and CSRF settings.
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Value("${security.public-paths}")
    private String[] publicPaths;

    /**
     * Configures a PasswordEncoder bean using BCryptPasswordEncoder for secure password hashing.
     * @return a PasswordEncoder instance that can be used for encoding and verifying passwords in the application.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for the application, defining CORS settings, disabling CSRF protection, setting session management to stateless, and specifying authorization rules for HTTP requests.
     * @param http the HttpSecurity object used to configure security settings for HTTP requests in the application.
     * @return a SecurityFilterChain instance that defines the security configuration for the application, including authentication and authorization rules, CORS settings, and session management policies.
     * @throws Exception if an error occurs while configuring the security filter chain, such as invalid configuration settings or issues with the JWT filter.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicPaths).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

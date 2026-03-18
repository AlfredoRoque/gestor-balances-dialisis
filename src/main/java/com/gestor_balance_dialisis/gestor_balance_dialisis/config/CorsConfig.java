package com.gestor_balance_dialisis.gestor_balance_dialisis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration class for setting up Cross-Origin Resource Sharing (CORS) in the application. This class defines a bean that configures CORS settings, allowing specified origins, methods, and headers to access the application's resources. The allowed origin patterns are read from the application properties, enabling flexibility in defining which origins can interact with the API.
 */
@Configuration
public class CorsConfig {

    @Value("${origin.patterns.path}")
    private String ORIGIN_PATTERN_PATH;

    /**
     * Defines a bean that configures CORS settings for the application. It allows specified origins, HTTP methods, and headers to access the application's resources. The configuration is applied to all endpoints (/**) in the application.
     * @return A CorsConfigurationSource bean that contains the CORS configuration for the application.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(ORIGIN_PATTERN_PATH));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

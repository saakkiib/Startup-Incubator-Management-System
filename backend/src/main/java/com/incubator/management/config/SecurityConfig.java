package com.incubator.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * ----------------------
 * Configures Spring Security for the application:
 *
 * - CSRF is disabled (safe for REST APIs using token-based auth)
 * - /api/auth/** endpoints are public (login & register must be accessible without a token)
 * - All other endpoints require authentication
 * - Provides a BCrypt password encoder bean used throughout the app
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the HTTP security filter chain.
     * Rules are applied in order — more specific rules should come first.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — not needed for stateless REST APIs
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Allow unauthenticated access to login and register endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // All other endpoints require the user to be logged in
                .anyRequest().authenticated()
            );
        return http.build();
    }

    /**
     * Provides a BCryptPasswordEncoder bean.
     * Used by UserService to hash passwords on registration
     * and verify them on login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

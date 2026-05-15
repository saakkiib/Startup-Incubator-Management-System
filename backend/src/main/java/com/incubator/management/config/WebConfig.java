package com.incubator.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * ---------------------
 * Configures cross-cutting web concerns for the application.
 *
 * Currently handles:
 * - CORS (Cross-Origin Resource Sharing) — allows the frontend
 *   (running on a different port or domain) to communicate with this API.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Allow all origins, methods, and headers for every API endpoint.
     *
     * NOTE: In production, replace allowedOrigins("*") with specific
     * frontend domains (e.g. "https://incubatorx.com") for better security.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")              // Apply to all endpoints
                .allowedOrigins("*")            // Allow requests from any origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");            // Allow all request headers
    }
}

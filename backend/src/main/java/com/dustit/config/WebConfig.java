package com.dustit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Without this, the browser blocks the frontend (opened as a local file,
 * or served from a different port) from calling the backend at all -
 * that's the browser's CORS policy, not a DUST-IT bug.
 *
 * Wide open for local development only. Once there's a real deployed
 * frontend URL, replace "*" with that specific URL instead.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
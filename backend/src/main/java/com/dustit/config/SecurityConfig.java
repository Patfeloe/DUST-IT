package com.dustit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures authentication for the API (build-order step 9).
 *
 * Same "works locally without cloud, real once configured" pattern used
 * for the database (step 2), S3 (step 5), and SQS (step 7): if
 * COGNITO_ISSUER_URI isn't set, every endpoint stays open so local
 * development and testing (see README curl examples) keep working
 * without needing a Cognito User Pool set up first. Once it IS set, the
 * API genuinely requires a valid Cognito-issued JWT for anything
 * touching student-specific data.
 *
 * This is NOT a toggle to leave off in a real deployment - it's a
 * bootstrapping convenience. A production DUST-IT deployment must have
 * COGNITO_ISSUER_URI set.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cognito.issuer-uri:}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        if (issuerUri == null || issuerUri.isBlank()) {
            // Local dev fallback - no Cognito configured yet.
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        JwtDecoder jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();

        http.authorizeHttpRequests(auth -> auth
                        // Browsing learning content doesn't require login -
                        // only actions tied to a specific student's identity do.
                        .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/topics/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/assessments/*/questions").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));

        return http.build();
    }
}

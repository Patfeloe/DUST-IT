package com.dustit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * The very first API endpoint for DUST-IT.
 *
 * Purpose: prove the whole chain works end to end before building any
 * real features — Spring Boot starts, the controller responds, and (once
 * the frontend calls this) the browser can reach the backend.
 *
 * Try it once running: GET http://localhost:8080/api/health
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "DUST-IT backend is running",
                "timestamp", Instant.now().toString()
        );
    }
}

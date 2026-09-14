package com.dustit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DUST-IT — Understand it. Practise it. Dust it.
 *
 * Entry point for the DUST-IT backend. This starts the embedded server
 * and boots up Spring's component scanning, which will pick up the
 * controllers, services, and repositories as they're added.
 */
@SpringBootApplication
public class DustItApplication {

    public static void main(String[] args) {
        SpringApplication.run(DustItApplication.class, args);
    }
}
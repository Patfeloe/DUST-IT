package com.dustit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Wires up the SQS client, same pattern as S3Config - no hardcoded
 * credentials, the SDK's default credential chain handles it.
 */
@Configuration
public class SqsConfig {

    @Value("${aws.region:af-south-1}")
    private String region;

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(region))
                .build();
    }
}
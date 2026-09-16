package com.dustit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Wires up the S3 client. No AWS access keys are hardcoded here or
 * anywhere else - the AWS SDK's default credential chain picks up
 * credentials from (in order) environment variables, the shared
 * ~/.aws/credentials file (set up via `aws configure`), or an IAM role
 * if this ends up running on AWS itself (e.g. ECS later). That's the
 * standard, secure way to do this - see Section 22 of the requirements
 * doc (secrets management).
 */
@Configuration
public class S3Config {

    @Value("${aws.region:af-south-1}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
}
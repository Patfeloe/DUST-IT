package com.dustit.service;

import com.dustit.dto.LearningEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Publishes learning events (Section 17) to the SQS queue, so a separate,
 * independently-scaling function (build-order step 8) can consume them
 * asynchronously - e.g. to recalculate concept-level competency after an
 * ASSESSMENT_COMPLETED event, without slowing down the student-facing
 * request that triggered it.
 */
@Service
public class EventPublisherService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queue-url:}")
    private String queueUrl;

    public EventPublisherService(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    public void publish(LearningEvent event) {
        if (queueUrl == null || queueUrl.isBlank()) {
            // No queue configured yet (e.g. local dev without AWS set up) -
            // skip publishing rather than fail the student-facing request.
            return;
        }

        try {
            String messageBody = objectMapper.writeValueAsString(event);

            sqsClient.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(messageBody)
                            .build()
            );
        } catch (Exception e) {
            // Never let a failed event publish break the actual student
            // action (e.g. completing an assessment) - log and move on.
            System.err.println("Failed to publish learning event: " + e.getMessage());
        }
    }
}
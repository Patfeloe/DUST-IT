package com.dustit.dto;

import com.dustit.model.LearningEventType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;

/**
 * The shape of a learning event, published as JSON to the SQS queue.
 *
 * `data` is deliberately a loose Map rather than a fixed set of fields,
 * since different event types carry different information (a
 * TOPIC_SEARCHED event has a search query; an ASSESSMENT_COMPLETED event
 * has an assessmentId, attemptId, and score). Consumers (the future
 * competency-calculation function, step 8) read the fields relevant to
 * the eventType they care about.
 */
public class LearningEvent {

    @NotNull(message = "eventType is required")
    private LearningEventType eventType;

    // Fallback only for local testing without Cognito configured - see
    // AuthUtil / SecurityConfig. Once real auth is in place, the
    // authenticated JWT's subject is used instead, regardless of this value.
    private String studentId;

    private Map<String, Object> data;

    private String timestamp;

    public LearningEvent() {
        // for JSON deserialization
    }

    public LearningEvent(LearningEventType eventType, String studentId, Map<String, Object> data) {
        this.eventType = eventType;
        this.studentId = studentId;
        this.data = data;
        this.timestamp = Instant.now().toString();
    }

    public LearningEventType getEventType() {
        return eventType;
    }

    public void setEventType(LearningEventType eventType) {
        this.eventType = eventType;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
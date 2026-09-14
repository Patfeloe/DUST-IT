package com.dustit.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * What the frontend sends when creating a topic. Kept separate from the
 * Topic entity so the API contract doesn't accidentally change every time
 * the database model changes.
 */
public class CreateTopicRequest {

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    @NotBlank(message = "subject is required")
    private String subject;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
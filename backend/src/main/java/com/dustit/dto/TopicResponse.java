package com.dustit.dto;

import com.dustit.model.Topic;

/**
 * What the API sends back for a topic. Same separation reasoning as
 * CreateTopicRequest.
 */
public class TopicResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String subject;

    public TopicResponse(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.description = topic.getDescription();
        this.subject = topic.getSubject();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSubject() {
        return subject;
    }
}
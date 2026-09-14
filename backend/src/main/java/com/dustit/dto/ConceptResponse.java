package com.dustit.dto;

import com.dustit.model.Concept;

public class ConceptResponse {

    private final Long id;
    private final Long topicId;
    private final String name;
    private final String description;

    public ConceptResponse(Concept concept) {
        this.id = concept.getId();
        this.topicId = concept.getTopic().getId();
        this.name = concept.getName();
        this.description = concept.getDescription();
    }

    public Long getId() {
        return id;
    }

    public Long getTopicId() {
        return topicId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

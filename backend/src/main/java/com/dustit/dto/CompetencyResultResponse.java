package com.dustit.dto;

import com.dustit.model.CompetencyResult;

public class CompetencyResultResponse {

    private final Long conceptId;
    private final String conceptName;
    private final Long topicId;
    private final String topicTitle;
    private final Double score;
    private final String status;
    private final String updatedAt;

    public CompetencyResultResponse(CompetencyResult result) {
        this.conceptId = result.getConcept().getId();
        this.conceptName = result.getConcept().getName();
        this.topicId = result.getConcept().getTopic().getId();
        this.topicTitle = result.getConcept().getTopic().getTitle();
        this.score = result.getScore();
        this.status = result.getStatus();
        this.updatedAt = result.getUpdatedAt().toString();
    }

    public Long getConceptId() {
        return conceptId;
    }

    public String getConceptName() {
        return conceptName;
    }

    public Long getTopicId() {
        return topicId;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public Double getScore() {
        return score;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}

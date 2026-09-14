package com.dustit.dto;

import com.dustit.model.Attempt;
import com.dustit.service.ScoreMessages;

public class AttemptResultResponse {

    private final Long id;
    private final Long assessmentId;
    private final Double score;
    private final String message;

    public AttemptResultResponse(Attempt attempt) {
        this.id = attempt.getId();
        this.assessmentId = attempt.getAssessment().getId();
        this.score = attempt.getScore();
        this.message = attempt.getScore() != null ? ScoreMessages.forScore(attempt.getScore()) : null;
    }

    public Long getId() {
        return id;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public Double getScore() {
        return score;
    }

    public String getMessage() {
        return message;
    }
}
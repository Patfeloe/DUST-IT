package com.dustit.dto;

import com.dustit.model.Attempt;

public class AttemptResponse {

    private final Long id;
    private final Long assessmentId;
    private final String studentId;
    private final String startedAt;
    private final String completedAt;
    private final Double score;

    public AttemptResponse(Attempt attempt) {
        this.id = attempt.getId();
        this.assessmentId = attempt.getAssessment().getId();
        this.studentId = attempt.getStudentId();
        this.startedAt = attempt.getStartedAt().toString();
        this.completedAt = attempt.getCompletedAt() != null ? attempt.getCompletedAt().toString() : null;
        this.score = attempt.getScore();
    }

    public Long getId() {
        return id;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public Double getScore() {
        return score;
    }
}

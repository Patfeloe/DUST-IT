package com.dustit.dto;

import com.dustit.model.Assessment;
import com.dustit.model.DifficultyLevel;

public class AssessmentResponse {

    private final Long id;
    private final Long topicId;
    private final String title;
    private final DifficultyLevel difficultyLevel;

    public AssessmentResponse(Assessment assessment) {
        this.id = assessment.getId();
        this.topicId = assessment.getTopic().getId();
        this.title = assessment.getTitle();
        this.difficultyLevel = assessment.getDifficultyLevel();
    }

    public Long getId() {
        return id;
    }

    public Long getTopicId() {
        return topicId;
    }

    public String getTitle() {
        return title;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }
}
package com.dustit.dto;

import jakarta.validation.constraints.NotNull;

public class SubmitAnswerRequest {

    @NotNull(message = "questionId is required")
    private Long questionId;

    @NotNull(message = "selectedOptionIndex is required")
    private Integer selectedOptionIndex;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public void setSelectedOptionIndex(Integer selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
    }
}
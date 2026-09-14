package com.dustit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateQuestionRequest {

    @NotBlank(message = "text is required")
    private String text;

    @NotEmpty(message = "options must have at least 2 entries")
    private List<String> options;

    @NotNull(message = "correctOptionIndex is required")
    private Integer correctOptionIndex;

    // IDs of the Concepts this question tests - Section 8: "A question
    // can therefore test one or more specific concepts."
    @NotEmpty(message = "conceptIds must have at least 1 entry")
    private List<Long> conceptIds;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Integer getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(Integer correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public List<Long> getConceptIds() {
        return conceptIds;
    }

    public void setConceptIds(List<Long> conceptIds) {
        this.conceptIds = conceptIds;
    }
}
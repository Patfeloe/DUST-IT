package com.dustit.dto;

import com.dustit.model.Answer;

public class AnswerResponse {

    private final Long id;
    private final Long questionId;
    private final int selectedOptionIndex;
    private final boolean correct;

    public AnswerResponse(Answer answer) {
        this.id = answer.getId();
        this.questionId = answer.getQuestion().getId();
        this.selectedOptionIndex = answer.getSelectedOptionIndex();
        this.correct = answer.isCorrect();
    }

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public boolean isCorrect() {
        return correct;
    }
}
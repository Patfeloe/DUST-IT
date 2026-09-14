package com.dustit.dto;

import com.dustit.model.Concept;
import com.dustit.model.Question;

import java.util.List;

/**
 * What a student sees for a question while taking an assessment.
 *
 * Deliberately does NOT include correctOptionIndex - leaking that here
 * would let a student inspect network requests and see every answer.
 * The correct answer is only ever checked server-side, in
 * AttemptService.submitAnswer().
 */
public class QuestionResponse {

    private final Long id;
    private final String text;
    private final List<String> options;
    private final List<Long> conceptIds;

    public QuestionResponse(Question question) {
        this.id = question.getId();
        this.text = question.getText();
        this.options = question.getOptions();
        this.conceptIds = question.getConcepts().stream().map(Concept::getId).toList();
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public List<Long> getConceptIds() {
        return conceptIds;
    }
}

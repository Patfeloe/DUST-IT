package com.dustit.model;

import jakarta.persistence.*;

/**
 * A single answer within an Attempt. Storing which specific question
 * (and therefore which concepts, via Question.getConcepts()) each answer
 * belongs to is exactly what lets DUST-IT calculate competency per
 * concept rather than just an overall percentage (Section 10).
 */
@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private Attempt attempt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private int selectedOptionIndex;

    @Column(nullable = false)
    private boolean correct;

    protected Answer() {
        // required by JPA
    }

    public Answer(Attempt attempt, Question question, int selectedOptionIndex, boolean correct) {
        this.attempt = attempt;
        this.question = question;
        this.selectedOptionIndex = selectedOptionIndex;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public Attempt getAttempt() {
        return attempt;
    }

    public Question getQuestion() {
        return question;
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public boolean isCorrect() {
        return correct;
    }
}

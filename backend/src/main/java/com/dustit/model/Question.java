package com.dustit.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A multiple-choice question within an Assessment.
 *
 * A question can test one or more Concepts (Section 8: "A question can
 * therefore test one or more specific concepts") - this many-to-many
 * relationship is exactly what makes concept-level competency scoring
 * possible: when a student gets a question wrong, DUST-IT knows which
 * specific concept(s) to flag, not just "the topic."
 */
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(nullable = false, length = 2000)
    private String text;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    @OrderColumn(name = "option_index")
    private List<String> options = new ArrayList<>();

    // Index into `options` that is the correct answer. Never expose this
    // in an API response shown to a student taking the assessment -
    // see QuestionResponse vs QuestionWithAnswerResponse.
    @Column(nullable = false)
    private int correctOptionIndex;

    @ManyToMany
    @JoinTable(
            name = "question_concepts",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "concept_id")
    )
    private Set<Concept> concepts = new HashSet<>();

    protected Question() {
        // required by JPA
    }

    public Question(Assessment assessment, String text, List<String> options, int correctOptionIndex) {
        this.assessment = assessment;
        this.text = text;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public Long getId() {
        return id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public Set<Concept> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<Concept> concepts) {
        this.concepts = concepts;
    }

    public boolean isCorrect(int selectedOptionIndex) {
        return selectedOptionIndex == correctOptionIndex;
    }
}

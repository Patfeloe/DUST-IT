package com.dustit.model;

import jakarta.persistence.*;

/**
 * An Assessment is one difficulty stage of a Topic (Beginner /
 * Intermediate / Advanced / Expert - Section 9). It contains Questions.
 */
@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficultyLevel;

    protected Assessment() {
        // required by JPA
    }

    public Assessment(Topic topic, String title, DifficultyLevel difficultyLevel) {
        this.topic = topic;
        this.title = title;
        this.difficultyLevel = difficultyLevel;
    }

    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public String getTitle() {
        return title;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }
}
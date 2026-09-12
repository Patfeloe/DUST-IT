package com.dustit.model;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * One student's attempt at an Assessment.
 *
 * studentId is a plain string for now, since real authentication
 * (Section 4.1, build-order step 9: managed identity via Cognito) hasn't
 * been built yet. Once it is, this becomes a real foreign key to a
 * Student/User entity - the rest of the assessment logic doesn't change.
 *
 * Section 9: "Students should be able to review and retry assessments" -
 * each retry is simply a new Attempt row, so history is preserved.
 */
@Entity
@Table(name = "attempts")
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant completedAt;

    // Overall percentage score, set once the attempt is completed.
    // Concept-level competency (Section 10) is calculated separately,
    // asynchronously, at build-order step 8 - this is just the raw score.
    private Double score;

    protected Attempt() {
        // required by JPA
    }

    public Attempt(Assessment assessment, String studentId) {
        this.assessment = assessment;
        this.studentId = studentId;
        this.startedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public String getStudentId() {
        return studentId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean isCompleted() {
        return completedAt != null;
    }
}

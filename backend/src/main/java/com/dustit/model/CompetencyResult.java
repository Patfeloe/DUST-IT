package com.dustit.model;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * The current competency snapshot for one (student, concept) pair -
 * Section 10 of the requirements doc.
 *
 * This entity is deliberately READ-ONLY from the backend's perspective.
 * Rows here are written by the competency-function Lambda (build-order
 * step 8), not by any code in this Spring Boot app - the backend just
 * serves this data via CompetencyController for the dashboard to display.
 *
 * Hibernate's ddl-auto=update creates this table the same way as any
 * other entity, which is convenient - the Lambda then writes into a
 * table the backend already created, using plain JDBC (see
 * competency-function/README.md for why it doesn't use JPA itself).
 */
@Entity
@Table(name = "competency_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "concept_id"}))
public class CompetencyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "concept_id", nullable = false)
    private Concept concept;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private String status;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CompetencyResult() {
        // required by JPA - rows are written by the Lambda, not this app
    }

    public Long getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public Concept getConcept() {
        return concept;
    }

    public Double getScore() {
        return score;
    }

    public String getStatus() {
        return status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
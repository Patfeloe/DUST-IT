package com.dustit.model;

import jakarta.persistence.*;

/**
 * A Concept is a specific sub-unit of a Topic (e.g. Topic "Consolidated
 * Financial Statements" has Concepts "Goodwill", "NCI", "Intercompany
 * transactions" - see Section 8 of the requirements doc).
 *
 * This is what makes concept-level competency (Section 10) possible:
 * questions test specific concepts, not just "the topic" as a whole.
 */
@Entity
@Table(name = "concepts")
public class Concept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    protected Concept() {
        // required by JPA
    }

    public Concept(Topic topic, String name, String description) {
        this.topic = topic;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

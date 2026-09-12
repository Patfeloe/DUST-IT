package com.dustit.model;

import jakarta.persistence.*;

/**
 * A Topic is the top-level thing a student searches for and learns
 * (e.g. "Consolidated Financial Statements", "Python Functions").
 *
 * This is the first real @Entity in DUST-IT - with JPA and
 * spring.jpa.hibernate.ddl-auto=update, Hibernate will create the
 * "topics" table automatically the next time the app starts.
 */
@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    // e.g. "Accounting", "Auditing", "Programming" - keeps DUST-IT from
    // being tied to one subject (see Section 3 of the requirements doc)
    @Column(nullable = false)
    private String subject;

    protected Topic() {
        // required by JPA
    }

    public Topic(String title, String description, String subject) {
        this.title = title;
        this.description = description;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
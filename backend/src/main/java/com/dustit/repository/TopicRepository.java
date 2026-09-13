package com.dustit.repository;

import com.dustit.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Talks to the database for Topic. Spring Data JPA generates the
 * implementation of these methods automatically - no SQL to write.
 */
public interface TopicRepository extends JpaRepository<Topic, Long> {

    // Used by topic search (Section 4.2) once natural-language search
    // is layered on top - for now, a simple case-insensitive match.
    List<Topic> findBySubjectIgnoreCase(String subject);
}

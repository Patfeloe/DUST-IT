package com.dustit.repository;

import com.dustit.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findBySubjectIgnoreCase(String subject);

    List<Topic> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrSubjectContainingIgnoreCase(
            String titleQuery, String descriptionQuery, String subjectQuery);
}



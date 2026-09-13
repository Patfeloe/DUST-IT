package com.dustit.repository;

import com.dustit.model.Concept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConceptRepository extends JpaRepository<Concept, Long> {

    List<Concept> findByTopicId(Long topicId);
}

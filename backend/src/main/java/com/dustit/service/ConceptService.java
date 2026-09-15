package com.dustit.service;

import com.dustit.dto.CreateConceptRequest;
import com.dustit.model.Concept;
import com.dustit.model.Topic;
import com.dustit.repository.ConceptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConceptService {

    private final ConceptRepository conceptRepository;
    private final TopicService topicService;

    public ConceptService(ConceptRepository conceptRepository, TopicService topicService) {
        this.conceptRepository = conceptRepository;
        this.topicService = topicService;
    }

    public List<Concept> getConceptsForTopic(Long topicId) {
        return conceptRepository.findByTopicId(topicId);
    }

    public Optional<Concept> createConcept(Long topicId, CreateConceptRequest request) {
        return topicService.getTopicById(topicId).map(topic -> {
            Concept concept = new Concept(topic, request.getName(), request.getDescription());
            return conceptRepository.save(concept);
        });
    }

    public List<Concept> findAllById(List<Long> ids) {
        return conceptRepository.findAllById(ids);
    }
}

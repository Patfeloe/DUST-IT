package com.dustit.controller;

import com.dustit.dto.ConceptResponse;
import com.dustit.dto.CreateConceptRequest;
import com.dustit.service.ConceptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *   GET  /api/topics/{topicId}/concepts   - list concepts for a topic
 *   POST /api/topics/{topicId}/concepts   - add a concept to a topic
 */
@RestController
public class ConceptController {

    private final ConceptService conceptService;

    public ConceptController(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    @GetMapping("/api/topics/{topicId}/concepts")
    public List<ConceptResponse> getConceptsForTopic(@PathVariable Long topicId) {
        return conceptService.getConceptsForTopic(topicId).stream()
                .map(ConceptResponse::new)
                .toList();
    }

    @PostMapping("/api/topics/{topicId}/concepts")
    public ResponseEntity<ConceptResponse> createConcept(
            @PathVariable Long topicId,
            @Valid @RequestBody CreateConceptRequest request) {

        return conceptService.createConcept(topicId, request)
                .map(concept -> ResponseEntity.status(HttpStatus.CREATED).body(new ConceptResponse(concept)))
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.dustit.controller;

import com.dustit.dto.CreateTopicRequest;
import com.dustit.dto.TopicResponse;
import com.dustit.model.Topic;
import com.dustit.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The first real feature endpoints in DUST-IT.
 *
 *   GET  /api/topics       - list all topics
 *   GET  /api/topics/{id}  - get one topic
 *   POST /api/topics       - create a topic
 */
@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public List<TopicResponse> getAllTopics() {
        return topicService.getAllTopics().stream()
                .map(TopicResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> getTopicById(@PathVariable Long id) {
        return topicService.getTopicById(id)
                .map(topic -> ResponseEntity.ok(new TopicResponse(topic)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(@Valid @RequestBody CreateTopicRequest request) {
        Topic created = topicService.createTopic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TopicResponse(created));
    }
}
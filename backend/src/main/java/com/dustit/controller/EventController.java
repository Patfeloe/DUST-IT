package com.dustit.controller;

import com.dustit.config.AuthUtil;
import com.dustit.dto.LearningEvent;
import com.dustit.service.EventPublisherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lets the frontend record the learning events that aren't already
 * published as a side effect of another action - e.g. TOPIC_SEARCHED,
 * RESOURCE_VIEWED, LESSON_STARTED, NOTE_SAVED, TOPIC_SKIPPED (Section 17).
 *
 * ASSESSMENT_COMPLETED is different - it's published automatically by
 * AttemptService when an attempt is completed, since the backend already
 * has all the details at that point.
 */
@RestController
public class EventController {

    private final EventPublisherService eventPublisherService;

    public EventController(EventPublisherService eventPublisherService) {
        this.eventPublisherService = eventPublisherService;
    }

    @PostMapping("/api/events")
    public ResponseEntity<Void> recordEvent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody LearningEvent event) {

        // Always resolve from the verified token when auth is configured -
        // never trust the studentId a client claims in the body once real
        // auth is in place (see AuthUtil).
        event.setStudentId(AuthUtil.resolveStudentId(jwt, event.getStudentId()));

        eventPublisherService.publish(event);
        return ResponseEntity.accepted().build();
    }
}
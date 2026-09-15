package com.dustit.controller;

import com.dustit.config.AuthUtil;
import com.dustit.dto.*;
import com.dustit.service.AttemptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 *   POST /api/assessments/{assessmentId}/attempts   - start an attempt
 *   POST /api/attempts/{id}/answers                 - submit an answer to one question
 *   POST /api/attempts/{id}/complete                - finish the attempt and get the score
 */
@RestController
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping("/api/assessments/{assessmentId}/attempts")
    public ResponseEntity<AttemptResponse> startAttempt(
            @PathVariable Long assessmentId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody(required = false) StartAttemptRequest request) {

        String fallbackStudentId = request != null ? request.getStudentId() : null;
        String studentId = AuthUtil.resolveStudentId(jwt, fallbackStudentId);

        return attemptService.startAttempt(assessmentId, studentId)
                .map(attempt -> ResponseEntity.status(HttpStatus.CREATED).body(new AttemptResponse(attempt)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/attempts/{id}/answers")
    public ResponseEntity<AnswerResponse> submitAnswer(
            @PathVariable Long id,
            @Valid @RequestBody SubmitAnswerRequest request) {

        var answer = attemptService.submitAnswer(id, request.getQuestionId(), request.getSelectedOptionIndex());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AnswerResponse(answer));
    }

    @PostMapping("/api/attempts/{id}/complete")
    public ResponseEntity<AttemptResultResponse> completeAttempt(@PathVariable Long id) {
        return attemptService.completeAttempt(id)
                .map(attempt -> ResponseEntity.ok(new AttemptResultResponse(attempt)))
                .orElse(ResponseEntity.notFound().build());
    }
}
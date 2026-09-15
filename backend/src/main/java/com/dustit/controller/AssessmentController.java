package com.dustit.controller;

import com.dustit.dto.*;
import com.dustit.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *   GET  /api/topics/{topicId}/assessments        - list assessments for a topic
 *   POST /api/topics/{topicId}/assessments         - create an assessment (Beginner/Intermediate/Advanced/Expert)
 *   GET  /api/assessments/{id}/questions           - list questions (no answers exposed)
 *   POST /api/assessments/{id}/questions           - add a question, linked to concept(s)
 */
@RestController
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping("/api/topics/{topicId}/assessments")
    public List<AssessmentResponse> getAssessmentsForTopic(@PathVariable Long topicId) {
        return assessmentService.getAssessmentsForTopic(topicId).stream()
                .map(AssessmentResponse::new)
                .toList();
    }

    @PostMapping("/api/topics/{topicId}/assessments")
    public ResponseEntity<AssessmentResponse> createAssessment(
            @PathVariable Long topicId,
            @Valid @RequestBody CreateAssessmentRequest request) {

        return assessmentService.createAssessment(topicId, request)
                .map(assessment -> ResponseEntity.status(HttpStatus.CREATED).body(new AssessmentResponse(assessment)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/assessments/{id}/questions")
    public List<QuestionResponse> getQuestionsForAssessment(@PathVariable Long id) {
        return assessmentService.getQuestionsForAssessment(id).stream()
                .map(QuestionResponse::new)
                .toList();
    }

    @PostMapping("/api/assessments/{id}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(
            @PathVariable Long id,
            @Valid @RequestBody CreateQuestionRequest request) {

        return assessmentService.addQuestion(id, request)
                .map(question -> ResponseEntity.status(HttpStatus.CREATED).body(new QuestionResponse(question)))
                .orElse(ResponseEntity.notFound().build());
    }
}
package com.dustit.controller;

import com.dustit.dto.CompetencyResultResponse;
import com.dustit.repository.CompetencyResultRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *   GET /api/students/{studentId}/competency - a student's current
 *   competency across every concept they've been assessed on.
 *
 * This is a pure read - all the actual calculation happens in the
 * competency-function Lambda (build-order step 8), asynchronously,
 * triggered by ASSESSMENT_COMPLETED events. By the time a student checks
 * their dashboard, the numbers here reflect whatever the Lambda has
 * processed so far - there can be a short lag between completing an
 * assessment and seeing the updated competency, which is the normal
 * tradeoff of an asynchronous, event-driven design.
 *
 * Access control (Section 22): once Cognito is configured, a student can
 * only view their OWN competency - the path variable must match the
 * verified JWT's subject, not just be trusted at face value. Without
 * that check, changing the URL would let anyone see anyone else's data.
 */
@RestController
public class CompetencyController {

    private final CompetencyResultRepository competencyResultRepository;

    public CompetencyController(CompetencyResultRepository competencyResultRepository) {
        this.competencyResultRepository = competencyResultRepository;
    }

    @GetMapping("/api/students/{studentId}/competency")
    public ResponseEntity<List<CompetencyResultResponse>> getCompetencyForStudent(
            @PathVariable String studentId,
            @AuthenticationPrincipal Jwt jwt) {

        if (jwt != null && !jwt.getSubject().equals(studentId)) {
            return ResponseEntity.status(403).build();
        }

        List<CompetencyResultResponse> results = competencyResultRepository.findByStudentId(studentId).stream()
                .map(CompetencyResultResponse::new)
                .toList();
        return ResponseEntity.ok(results);
    }
}
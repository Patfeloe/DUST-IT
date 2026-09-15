package com.dustit.service;

import com.dustit.dto.LearningEvent;
import com.dustit.exception.InvalidAssessmentOperationException;
import com.dustit.model.*;
import com.dustit.repository.AnswerRepository;
import com.dustit.repository.AttemptRepository;
import com.dustit.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The core assessment-taking flow: start an attempt, answer questions
 * one at a time, then complete it and get a score.
 *
 * Note what this does NOT do itself: calculate concept-level competency
 * (Section 10). Completing an attempt publishes an ASSESSMENT_COMPLETED
 * event instead - a separate, independently-scaling function (build-order
 * step 8) consumes that event and does the concept-level calculation.
 * This keeps the student-facing request fast, since it doesn't wait on
 * that heavier analysis.
 */
@Service
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentService assessmentService;
    private final EventPublisherService eventPublisherService;

    public AttemptService(AttemptRepository attemptRepository,
                          AnswerRepository answerRepository,
                          QuestionRepository questionRepository,
                          AssessmentService assessmentService,
                          EventPublisherService eventPublisherService) {
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.assessmentService = assessmentService;
        this.eventPublisherService = eventPublisherService;
    }

    public Optional<Attempt> startAttempt(Long assessmentId, String studentId) {
        return assessmentService.getAssessmentById(assessmentId)
                .map(assessment -> attemptRepository.save(new Attempt(assessment, studentId)));
    }

    public Optional<Attempt> getAttemptById(Long id) {
        return attemptRepository.findById(id);
    }

    public Answer submitAnswer(Long attemptId, Long questionId, int selectedOptionIndex) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new InvalidAssessmentOperationException("Attempt not found: " + attemptId));

        if (attempt.isCompleted()) {
            throw new InvalidAssessmentOperationException("This attempt is already completed - start a new attempt to retry.");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new InvalidAssessmentOperationException("Question not found: " + questionId));

        if (!question.getAssessment().getId().equals(attempt.getAssessment().getId())) {
            throw new InvalidAssessmentOperationException("This question does not belong to the attempt's assessment.");
        }

        boolean correct = question.isCorrect(selectedOptionIndex);
        Answer answer = new Answer(attempt, question, selectedOptionIndex, correct);
        return answerRepository.save(answer);
    }

    /**
     * Completes the attempt, calculates the overall raw score, and
     * publishes an ASSESSMENT_COMPLETED event carrying everything a
     * downstream consumer needs to calculate concept-level competency:
     * which question was answered, whether it was correct, and which
     * concept(s) that question tests.
     *
     * Section 11's encouraging score messages are generated on the
     * response layer from this score - see AttemptController.
     */
    public Optional<Attempt> completeAttempt(Long attemptId) {
        return attemptRepository.findById(attemptId).map(attempt -> {
            if (attempt.isCompleted()) {
                return attempt;
            }

            List<Answer> answers = answerRepository.findByAttemptId(attemptId);
            long totalQuestions = assessmentService.getQuestionsForAssessment(attempt.getAssessment().getId()).size();
            long correctAnswers = answers.stream().filter(Answer::isCorrect).count();

            double score = totalQuestions == 0 ? 0.0 : (correctAnswers * 100.0) / totalQuestions;

            attempt.setScore(score);
            attempt.setCompletedAt(Instant.now());
            Attempt saved = attemptRepository.save(attempt);

            publishAssessmentCompletedEvent(saved, answers);

            return saved;
        });
    }

    private void publishAssessmentCompletedEvent(Attempt attempt, List<Answer> answers) {
        List<Map<String, Object>> answerDetails = answers.stream()
                .map(answer -> Map.<String, Object>of(
                        "questionId", answer.getQuestion().getId(),
                        "conceptIds", answer.getQuestion().getConcepts().stream().map(Concept::getId).toList(),
                        "correct", answer.isCorrect()
                ))
                .toList();

        LearningEvent event = new LearningEvent(
                LearningEventType.ASSESSMENT_COMPLETED,
                attempt.getStudentId(),
                Map.of(
                        "attemptId", attempt.getId(),
                        "assessmentId", attempt.getAssessment().getId(),
                        "topicId", attempt.getAssessment().getTopic().getId(),
                        "score", attempt.getScore(),
                        "answers", answerDetails
                )
        );

        eventPublisherService.publish(event);
    }
}

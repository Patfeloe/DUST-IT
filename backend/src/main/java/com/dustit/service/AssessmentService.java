package com.dustit.service;

import com.dustit.dto.CreateAssessmentRequest;
import com.dustit.dto.CreateQuestionRequest;
import com.dustit.model.Assessment;
import com.dustit.model.Concept;
import com.dustit.model.Question;
import com.dustit.model.Topic;
import com.dustit.repository.AssessmentRepository;
import com.dustit.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final TopicService topicService;
    private final ConceptService conceptService;

    public AssessmentService(AssessmentRepository assessmentRepository,
                             QuestionRepository questionRepository,
                             TopicService topicService,
                             ConceptService conceptService) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.topicService = topicService;
        this.conceptService = conceptService;
    }

    public List<Assessment> getAssessmentsForTopic(Long topicId) {
        return assessmentRepository.findByTopicId(topicId);
    }

    public Optional<Assessment> getAssessmentById(Long id) {
        return assessmentRepository.findById(id);
    }

    public Optional<Assessment> createAssessment(Long topicId, CreateAssessmentRequest request) {
        return topicService.getTopicById(topicId).map(topic -> {
            Assessment assessment = new Assessment(topic, request.getTitle(), request.getDifficultyLevel());
            return assessmentRepository.save(assessment);
        });
    }

    public List<Question> getQuestionsForAssessment(Long assessmentId) {
        return questionRepository.findByAssessmentId(assessmentId);
    }

    public Optional<Question> addQuestion(Long assessmentId, CreateQuestionRequest request) {
        return getAssessmentById(assessmentId).map(assessment -> {
            Question question = new Question(
                    assessment,
                    request.getText(),
                    request.getOptions(),
                    request.getCorrectOptionIndex()
            );
            Set<Concept> concepts = new HashSet<>(conceptService.findAllById(request.getConceptIds()));
            question.setConcepts(concepts);
            return questionRepository.save(question);
        });
    }
}
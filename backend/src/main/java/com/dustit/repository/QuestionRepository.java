package com.dustit.repository;

import com.dustit.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByAssessmentId(Long assessmentId);
}

package com.dustit.repository;

import com.dustit.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByAttemptId(Long attemptId);

    Optional<Answer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
}
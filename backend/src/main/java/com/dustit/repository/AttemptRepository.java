package com.dustit.repository;

import com.dustit.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    List<Attempt> findByStudentId(String studentId);

    List<Attempt> findByAssessmentIdAndStudentId(Long assessmentId, String studentId);
}

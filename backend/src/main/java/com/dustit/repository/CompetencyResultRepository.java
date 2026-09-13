package com.dustit.repository;

import com.dustit.model.CompetencyResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetencyResultRepository extends JpaRepository<CompetencyResult, Long> {

    List<CompetencyResult> findByStudentId(String studentId);
}
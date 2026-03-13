package com.skct.domain.exam.repository;

import com.skct.domain.exam.entity.ExamPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamPaperRepository extends JpaRepository<ExamPaper, Long> {
    Page<ExamPaper> findByExamTypeAndActiveTrue(String examType, Pageable pageable);
    Page<ExamPaper> findByActiveTrue(Pageable pageable);
    Optional<ExamPaper> findByIdAndActiveTrue(Long id);
}

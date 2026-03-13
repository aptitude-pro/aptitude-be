package com.skct.domain.result.repository;

import com.skct.domain.result.entity.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    Page<ExamResult> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<ExamResult> findByUserIdOrderByCreatedAtAsc(Long userId);
    List<ExamResult> findByUserIdAndExamTypeOrderByCreatedAtAsc(Long userId, String examType);
    Optional<ExamResult> findBySessionId(Long sessionId);
    Optional<ExamResult> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT AVG(r.totalScore) FROM ExamResult r WHERE r.userId = :userId")
    Double findAvgScoreByUserId(Long userId);

    @Query("SELECT MAX(r.totalScore) FROM ExamResult r WHERE r.userId = :userId")
    Integer findMaxScoreByUserId(Long userId);

    @Query("SELECT COUNT(r) FROM ExamResult r WHERE r.userId = :userId")
    Long countByUserId(Long userId);
}

package com.skct.domain.session.repository;

import com.skct.domain.session.entity.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    Optional<ExamSession> findByIdAndUserId(Long id, Long userId);
    List<ExamSession> findByUserIdOrderByCreatedAtDesc(Long userId);
}

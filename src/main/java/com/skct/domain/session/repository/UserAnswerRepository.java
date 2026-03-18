package com.skct.domain.session.repository;

import com.skct.domain.session.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findBySessionId(Long sessionId);
    List<UserAnswer> findByExamResultId(Long examResultId);
    Optional<UserAnswer> findBySessionIdAndQuestionNo(Long sessionId, Integer questionNo);
    void deleteBySessionId(Long sessionId);
    void deleteByExamResultId(Long examResultId);
}

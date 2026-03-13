package com.skct.domain.exam.repository;

import com.skct.domain.exam.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExamPaperIdOrderByQuestionNo(Long examPaperId);
    Optional<Question> findByExamPaperIdAndQuestionNo(Long examPaperId, Integer questionNo);
    long countByExamPaperId(Long examPaperId);
}

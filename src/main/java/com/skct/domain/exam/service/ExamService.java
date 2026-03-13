package com.skct.domain.exam.service;

import com.skct.domain.exam.dto.ExamPaperResponse;
import com.skct.domain.exam.entity.ExamPaper;
import com.skct.domain.exam.entity.Question;
import com.skct.domain.exam.repository.ExamPaperRepository;
import com.skct.domain.exam.repository.QuestionRepository;
import com.skct.global.exception.CustomException;
import com.skct.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {

    private final ExamPaperRepository examPaperRepository;
    private final QuestionRepository questionRepository;

    public Page<ExamPaperResponse> getExamList(String examType, Pageable pageable) {
        Page<ExamPaper> papers = (examType != null && !examType.isBlank())
                ? examPaperRepository.findByExamTypeAndActiveTrue(examType, pageable)
                : examPaperRepository.findByActiveTrue(pageable);
        return papers.map(ExamPaperResponse::from);
    }

    public ExamPaperResponse getExam(Long examId) {
        ExamPaper paper = examPaperRepository.findByIdAndActiveTrue(examId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));
        return ExamPaperResponse.from(paper);
    }

    @Transactional
    public ExamPaperResponse createExam(Long userId, String title, String examType,
                                        Integer totalQuestions, Integer timeLimit,
                                        String description, String pdfUrl,
                                        List<Map<String, Object>> questions) {
        ExamPaper paper = ExamPaper.builder()
                .title(title)
                .examType(examType)
                .totalQuestions(totalQuestions)
                .timeLimit(timeLimit)
                .description(description)
                .pdfUrl(pdfUrl)
                .uploadedBy(userId)
                .build();

        paper = examPaperRepository.save(paper);

        if (questions != null) {
            final Long paperId = paper.getId();
            questions.forEach(q -> {
                Question question = Question.builder()
                        .examPaperId(paperId)
                        .questionNo((Integer) q.get("questionNo"))
                        .correctAnswer((Integer) q.get("correctAnswer"))
                        .category((String) q.getOrDefault("category", ""))
                        .build();
                questionRepository.save(question);
            });
        }

        return ExamPaperResponse.from(paper);
    }

    public List<Question> getQuestions(Long examPaperId) {
        return questionRepository.findByExamPaperIdOrderByQuestionNo(examPaperId);
    }
}

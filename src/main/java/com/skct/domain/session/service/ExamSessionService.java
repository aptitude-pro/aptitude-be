package com.skct.domain.session.service;

import com.skct.domain.exam.entity.ExamPaper;
import com.skct.domain.exam.entity.Question;
import com.skct.domain.exam.repository.ExamPaperRepository;
import com.skct.domain.exam.repository.QuestionRepository;
import com.skct.domain.result.entity.ExamResult;
import com.skct.domain.result.repository.ExamResultRepository;
import com.skct.domain.session.entity.ExamSession;
import com.skct.domain.session.entity.UserAnswer;
import com.skct.domain.session.repository.ExamSessionRepository;
import com.skct.domain.session.repository.UserAnswerRepository;
import com.skct.global.exception.CustomException;
import com.skct.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamSessionService {

    private final ExamSessionRepository sessionRepository;
    private final UserAnswerRepository answerRepository;
    private final ExamPaperRepository examPaperRepository;
    private final QuestionRepository questionRepository;
    private final ExamResultRepository resultRepository;

    @Transactional
    public SessionResponse startSession(Long userId, Long examPaperId) {
        ExamPaper paper = examPaperRepository.findByIdAndActiveTrue(examPaperId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));

        ExamSession session = ExamSession.builder()
                .userId(userId)
                .examPaperId(examPaperId)
                .startedAt(LocalDateTime.now())
                .build();

        session = sessionRepository.save(session);
        return SessionResponse.from(session, paper, List.of());
    }

    public SessionResponse getSession(Long sessionId, Long userId) {
        ExamSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        ExamPaper paper = examPaperRepository.findById(session.getExamPaperId())
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));
        List<UserAnswer> answers = answerRepository.findBySessionId(sessionId);
        return SessionResponse.from(session, paper, answers);
    }

    @Transactional
    public void saveAnswers(Long sessionId, Long userId, List<Map<String, Object>> answers) {
        ExamSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        if (session.getStatus() != ExamSession.Status.IN_PROGRESS) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_SUBMITTED);
        }

        for (Map<String, Object> a : answers) {
            Integer questionNo = (Integer) a.get("questionNo");
            Integer selectedAnswer = (Integer) a.get("selectedAnswer");

            answerRepository.findBySessionIdAndQuestionNo(sessionId, questionNo)
                    .ifPresentOrElse(
                            existing -> existing.updateAnswer(selectedAnswer),
                            () -> answerRepository.save(UserAnswer.builder()
                                    .sessionId(sessionId)
                                    .questionNo(questionNo)
                                    .selectedAnswer(selectedAnswer)
                                    .build())
                    );
        }
    }

    @Transactional
    public void saveMemo(Long sessionId, Long userId, Map<String, Object> memoData) {
        ExamSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        session.updateMemoData(memoData);
    }

    @Transactional
    public ExamResult submitSession(Long sessionId, Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        ExamSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (session.getStatus() == ExamSession.Status.SUBMITTED ||
                session.getStatus() == ExamSession.Status.COMPLETED) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_SUBMITTED);
        }

        session.updateStatus(ExamSession.Status.SUBMITTED);

        // 채점
        ExamPaper paper = examPaperRepository.findById(session.getExamPaperId())
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));
        List<Question> questions = questionRepository.findByExamPaperIdOrderByQuestionNo(paper.getId());
        List<UserAnswer> userAnswers = answerRepository.findBySessionId(sessionId);

        Map<Integer, Integer> answerMap = userAnswers.stream()
                .collect(Collectors.toMap(UserAnswer::getQuestionNo, UserAnswer::getSelectedAnswer));

        int correctCount = 0;
        Map<String, int[]> categoryStats = new HashMap<>();

        for (Question q : questions) {
            String cat = q.getCategory() != null ? q.getCategory() : "기타";
            categoryStats.putIfAbsent(cat, new int[]{0, 0}); // [correct, total]
            categoryStats.get(cat)[1]++;

            Integer selected = answerMap.get(q.getQuestionNo());
            if (selected != null && selected.equals(q.getCorrectAnswer())) {
                correctCount++;
                categoryStats.get(cat)[0]++;
            }
        }

        int totalScore = questions.isEmpty() ? 0 : (int) Math.round((double) correctCount / questions.size() * 100);

        Map<String, Integer> categoryScores = categoryStats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()[1] > 0 ? (int) Math.round((double) e.getValue()[0] / e.getValue()[1] * 100) : 0
                ));

        ExamResult result = ExamResult.builder()
                .userId(userId)
                .sessionId(sessionId)
                .examPaperId(paper.getId())
                .examType(paper.getExamType())
                .examTitle(paper.getTitle())
                .totalScore(totalScore)
                .correctCount(correctCount)
                .totalCount(questions.size())
                .elapsedSeconds(session.getElapsedSeconds())
                .categoryScores(categoryScores)
                .build();

        session.updateStatus(ExamSession.Status.COMPLETED);
        return resultRepository.save(result);
    }

    @Transactional
    public void deleteSession(Long sessionId, Long userId) {
        ExamSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (session.getStatus() == ExamSession.Status.COMPLETED ||
                session.getStatus() == ExamSession.Status.SUBMITTED) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_SUBMITTED);
        }

        answerRepository.deleteBySessionId(sessionId);
        sessionRepository.delete(session);
    }

    @Getter
    @Builder
    public static class SessionResponse {
        private Long id;
        private Long examPaperId;
        private String examTitle;
        private String examType;
        private String status;
        private Integer timeLimit;
        private Integer totalQuestions;
        private Map<String, Object> memoData;
        private List<AnswerDto> answers;

        @Getter
        @Builder
        public static class AnswerDto {
            private Integer questionNo;
            private Integer selectedAnswer;
        }

        public static SessionResponse from(ExamSession session, ExamPaper paper, List<UserAnswer> answers) {
            return SessionResponse.builder()
                    .id(session.getId())
                    .examPaperId(paper.getId())
                    .examTitle(paper.getTitle())
                    .examType(paper.getExamType())
                    .status(session.getStatus().name())
                    .timeLimit(paper.getTimeLimit())
                    .totalQuestions(paper.getTotalQuestions())
                    .memoData(session.getMemoData())
                    .answers(answers.stream()
                            .map(a -> AnswerDto.builder()
                                    .questionNo(a.getQuestionNo())
                                    .selectedAnswer(a.getSelectedAnswer())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        }
    }
}

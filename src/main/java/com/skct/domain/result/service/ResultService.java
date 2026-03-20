package com.skct.domain.result.service;

import com.skct.domain.exam.entity.Question;
import com.skct.domain.exam.repository.ExamPaperRepository;
import com.skct.domain.exam.repository.QuestionRepository;
import com.skct.domain.my.repository.MyLogRepository;
import com.skct.domain.result.dto.ExamResultResponse;
import com.skct.domain.result.dto.ManualResultRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResultService {

    private final ExamResultRepository resultRepository;
    private final UserAnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final ExamSessionRepository sessionRepository;
    private final ExamPaperRepository examPaperRepository;
    private final MyLogRepository myLogRepository;

    public Page<ExamResultResponse> getResults(Long userId, Pageable pageable) {
        return resultRepository.findByUserIdAndIsDraftFalseOrderByCreatedAtDesc(userId, pageable)
                .map(r -> ExamResultResponse.from(r, null));
    }

    public ExamResultResponse getResult(Long resultId, Long userId) {
        ExamResult result = resultRepository.findByIdAndUserId(resultId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESULT_NOT_FOUND));

        List<UserAnswer> userAnswers;
        if (result.getSessionId() != null) {
            userAnswers = answerRepository.findBySessionId(result.getSessionId());
            // examResultId로 저장된 marks(isGuessed/isWrong)를 sessionId 답안에 overlay
            List<UserAnswer> markedAnswers = answerRepository.findByExamResultId(resultId);
            if (!markedAnswers.isEmpty()) {
                Map<Integer, UserAnswer> marksMap = markedAnswers.stream()
                        .collect(Collectors.toMap(UserAnswer::getQuestionNo, ua -> ua));
                userAnswers = userAnswers.stream()
                        .map(ua -> {
                            UserAnswer mark = marksMap.get(ua.getQuestionNo());
                            if (mark != null && (mark.isGuessed() || mark.isWrong())) {
                                return ua.withMarks(mark.isGuessed(), mark.isWrong());
                            }
                            return ua;
                        })
                        .collect(Collectors.toList());
            }
        } else {
            userAnswers = answerRepository.findByExamResultId(resultId);
        }

        List<Question> questions = result.getExamPaperId() != null
                ? questionRepository.findByExamPaperIdOrderByQuestionNo(result.getExamPaperId())
                : List.of();

        Map<Integer, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getQuestionNo, q -> q));

        List<ExamResultResponse.AnswerDetail> details = userAnswers.stream()
                .map(ua -> {
                    Question q = questionMap.get(ua.getQuestionNo());
                    Integer correctAnswer = q != null ? q.getCorrectAnswer() : null;
                    return ExamResultResponse.AnswerDetail.builder()
                            .questionNo(ua.getQuestionNo())
                            .selectedAnswer(ua.getSelectedAnswer())
                            .correctAnswer(correctAnswer)
                            .isCorrect(correctAnswer != null && correctAnswer.equals(ua.getSelectedAnswer()))
                            .isGuessed(ua.isGuessed())
                            .isWrong(ua.isWrong())
                            .category(q != null ? q.getCategory() : null)
                            .build();
                })
                .sorted(Comparator.comparingInt(ExamResultResponse.AnswerDetail::getQuestionNo))
                .collect(Collectors.toList());

        return ExamResultResponse.from(result, details);
    }

    public List<GrowthDataPoint> getGrowthData(Long userId, String examType) {
        List<ExamResult> results = resultRepository.findByUserIdAndIsDraftFalseOrderByCreatedAtAsc(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

        return results.stream()
                .filter(r -> examType == null || examType.isBlank() || examType.equals(r.getExamType()))
                .map(r -> GrowthDataPoint.builder()
                        .date(r.getCreatedAt() != null ? r.getCreatedAt().format(formatter) : "")
                        .score(r.getTotalScore())
                        .examType(r.getExamType())
                        .categoryScores(r.getCategoryScores())
                        .build())
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getCategoryData(Long userId) {
        List<ExamResult> results = resultRepository.findByUserIdAndIsDraftFalseOrderByCreatedAtAsc(userId);

        Map<String, List<Integer>> categoryAccum = new HashMap<>();
        for (ExamResult r : results) {
            if (r.getCategoryScores() != null) {
                r.getCategoryScores().forEach((cat, score) -> {
                    categoryAccum.computeIfAbsent(cat, k -> new ArrayList<>()).add(score);
                });
            }
        }

        return categoryAccum.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (int) Math.round(e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0))
                ));
    }

    public StatsSummary getStatsSummary(Long userId) {
        long count = resultRepository.countByUserId(userId);
        Double avg = resultRepository.findAvgScoreByUserId(userId);
        Integer max = resultRepository.findMaxScoreByUserId(userId);

        // 전체 정답률
        List<ExamResult> results = resultRepository.findByUserIdAndIsDraftFalseOrderByCreatedAtAsc(userId);
        int totalCorrect = results.stream().mapToInt(ExamResult::getCorrectCount).sum();
        int totalQuestions = results.stream().mapToInt(ExamResult::getTotalCount).sum();
        int correctRate = totalQuestions > 0 ? (int) Math.round((double) totalCorrect / totalQuestions * 100) : 0;

        return StatsSummary.builder()
                .totalCount((int) count)
                .avgScore(avg != null ? (int) Math.round(avg) : 0)
                .maxScore(max != null ? max : 0)
                .correctRate(correctRate)
                .build();
    }

    @Transactional
    public void deleteResult(Long resultId, Long userId) {
        ExamResult result = resultRepository.findByIdAndUserId(resultId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESULT_NOT_FOUND));
        resultRepository.delete(result);
    }

    @Transactional
    public ExamResultResponse createManualResult(Long userId, ManualResultRequest req) {
        Integer elapsedSeconds = null;

        if (req.getSessionId() != null) {
            ExamSession session = sessionRepository.findById(req.getSessionId())
                    .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
            session.updateStatus(ExamSession.Status.SUBMITTED);
            elapsedSeconds = req.getElapsedSeconds() != null
                    ? req.getElapsedSeconds()
                    : session.getElapsedSeconds();
        } else {
            elapsedSeconds = req.getElapsedSeconds();
        }

        List<ManualResultRequest.QuestionRecord> questions =
                req.getQuestions() != null ? req.getQuestions() : List.of();
        int answeredCount = (int) questions.stream()
                .filter(q -> q.getSelectedAnswer() != null)
                .count();

        ExamResult result = ExamResult.builder()
                .userId(userId)
                .sessionId(req.getSessionId())
                .examPaperId(null)
                .examType("SKCT")
                .examTitle(buildTitle(req))
                .totalScore(req.getTotalScore() != null ? req.getTotalScore() : 0)
                .correctCount(answeredCount)
                .totalCount(100)
                .elapsedSeconds(elapsedSeconds)
                .categoryScores(req.getCategoryScores())
                .examYear(req.getExamYear())
                .examPeriod(req.getExamPeriod())
                .platform(req.getPlatform())
                .examRound(req.getExamRound())
                .isDraft(req.isDraft())
                .build();

        ExamResult saved = resultRepository.save(result);

        if (!questions.isEmpty()) {
            Long savedId = saved.getId();
            List<UserAnswer> userAnswers = questions.stream()
                    .map(q -> UserAnswer.builder()
                            .examResultId(savedId)
                            .questionNo(q.getQuestionNo())
                            .selectedAnswer(q.getSelectedAnswer())
                            .isGuessed(q.isGuessed())
                            .isWrong(q.isWrong())
                            .build())
                    .collect(Collectors.toList());
            answerRepository.saveAll(userAnswers);
        }

        List<ExamResultResponse.AnswerDetail> details = ExamResultResponse.toDetails(
                answerRepository.findByExamResultId(saved.getId()));
        return ExamResultResponse.from(saved, details);
    }

    @Transactional
    public ExamResultResponse updateManualResult(Long userId, Long resultId, ManualResultRequest req) {
        ExamResult result = resultRepository.findByIdAndUserId(resultId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESULT_NOT_FOUND));

        Map<String, Integer> catScores = req.getCategoryScores() != null ? req.getCategoryScores() : Map.of();
        int total = catScores.values().stream().mapToInt(Integer::intValue).sum();

        List<ManualResultRequest.QuestionRecord> questions =
                req.getQuestions() != null ? req.getQuestions() : List.of();
        int answeredCount = (int) questions.stream()
                .filter(q -> q.getSelectedAnswer() != null)
                .count();

        result.updateScore(total, catScores, req.isDraft(),
                answeredCount > 0 ? answeredCount : result.getCorrectCount(),
                result.getTotalCount(),
                req.getElapsedSeconds());

        if (!questions.isEmpty()) {
            answerRepository.deleteByExamResultId(resultId);
            List<UserAnswer> userAnswers = questions.stream()
                    .map(q -> UserAnswer.builder()
                            .examResultId(resultId)
                            .questionNo(q.getQuestionNo())
                            .selectedAnswer(q.getSelectedAnswer())
                            .isGuessed(q.isGuessed())
                            .isWrong(q.isWrong())
                            .build())
                    .collect(Collectors.toList());
            answerRepository.saveAll(userAnswers);
        }

        List<ExamResultResponse.AnswerDetail> details = ExamResultResponse.toDetails(
                answerRepository.findByExamResultId(resultId));
        return ExamResultResponse.from(result, details);
    }

    @Transactional(readOnly = true)
    public List<ActivityDay> getActivityHeatmap(Long userId, LocalDate from, LocalDate to) {
        Set<LocalDate> myLogDates = myLogRepository
                .findByUserIdAndLogDateBetween(userId, from, to)
                .stream().map(l -> l.getLogDate()).collect(Collectors.toSet());

        Map<LocalDate, Long> examCounts = resultRepository
                .findByUserIdAndCreatedAtBetween(userId,
                        from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                .stream()
                .filter(r -> !r.isDraft())
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate(), Collectors.counting()));

        Set<LocalDate> allDates = new HashSet<>();
        allDates.addAll(myLogDates);
        allDates.addAll(examCounts.keySet());

        return allDates.stream().sorted().map(date -> {
            boolean hasLog = myLogDates.contains(date);
            int exams = examCounts.getOrDefault(date, 0L).intValue();
            int level = (hasLog ? 1 : 0) + Math.min(exams, 3);
            return ActivityDay.builder()
                    .date(date).hasLog(hasLog)
                    .examCount(exams).level(level).build();
        }).collect(Collectors.toList());
    }

    private String buildTitle(ManualResultRequest req) {
        if (req.getExamYear() != null && req.getExamPeriod() != null && req.getPlatform() != null) {
            String round = req.getExamRound() != null ? " " + req.getExamRound() : "";
            return String.format("%d년 %s %s%s SKCT", req.getExamYear(), req.getExamPeriod(), req.getPlatform(), round);
        }
        return "SKCT";
    }

    @Getter
    @Builder
    public static class ActivityDay {
        private LocalDate date;
        private boolean hasLog;
        private int examCount;
        private int level;
    }

    @Getter
    @Builder
    public static class GrowthDataPoint {
        private String date;
        private Integer score;
        private String examType;
        private Map<String, Integer> categoryScores;
    }

    @Getter
    @Builder
    public static class StatsSummary {
        private Integer totalCount;
        private Integer avgScore;
        private Integer maxScore;
        private Integer correctRate;
    }
}

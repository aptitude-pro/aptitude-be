package com.skct.domain.result.dto;

import com.skct.domain.result.entity.ExamResult;
import com.skct.domain.session.entity.UserAnswer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class ExamResultResponse {
    private Long id;
    private Long sessionId;
    private String examTitle;
    private String examType;
    private Integer totalScore;
    private Integer correctCount;
    private Integer totalCount;
    private Integer elapsedSeconds;
    private Double correctRate;
    private Map<String, Integer> categoryScores;
    private List<AnswerDetail> answers;
    private LocalDateTime createdAt;
    private Integer examYear;
    private String examPeriod;
    private String platform;
    private String examRound;

    @Getter
    @Builder
    public static class AnswerDetail {
        private Integer questionNo;
        private Integer selectedAnswer;
        private Integer correctAnswer;
        private boolean isCorrect;
        private String category;
    }

    public static ExamResultResponse from(ExamResult result, List<AnswerDetail> answers) {
        double correctRate = result.getTotalCount() > 0
                ? (double) result.getCorrectCount() / result.getTotalCount() * 100
                : 0;

        return ExamResultResponse.builder()
                .id(result.getId())
                .sessionId(result.getSessionId())
                .examTitle(result.getExamTitle())
                .examType(result.getExamType())
                .totalScore(result.getTotalScore())
                .correctCount(result.getCorrectCount())
                .totalCount(result.getTotalCount())
                .elapsedSeconds(result.getElapsedSeconds())
                .correctRate(Math.round(correctRate * 10.0) / 10.0)
                .categoryScores(result.getCategoryScores())
                .answers(answers)
                .createdAt(result.getCreatedAt())
                .examYear(result.getExamYear())
                .examPeriod(result.getExamPeriod())
                .platform(result.getPlatform())
                .examRound(result.getExamRound())
                .build();
    }
}

package com.skct.domain.result.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ManualResultRequest {
    private Long sessionId;
    private Map<String, Integer> categoryScores;
    private Integer totalScore;
    private Integer examYear;
    private String examPeriod;
    private String platform;
    private String examRound;
    private Integer elapsedSeconds;
    private List<QuestionRecord> questions;
    private boolean isDraft;

    @Getter
    @NoArgsConstructor
    public static class QuestionRecord {
        private Integer questionNo;
        private Integer selectedAnswer;
        private boolean isGuessed;
        private boolean isWrong;
    }
}

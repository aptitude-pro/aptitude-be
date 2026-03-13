package com.skct.domain.result.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

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
}

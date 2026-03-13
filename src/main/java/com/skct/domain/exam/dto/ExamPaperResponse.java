package com.skct.domain.exam.dto;

import com.skct.domain.exam.entity.ExamPaper;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExamPaperResponse {
    private Long id;
    private String title;
    private String examType;
    private String pdfUrl;
    private Integer totalQuestions;
    private Integer timeLimit;
    private String description;
    private LocalDateTime createdAt;

    public static ExamPaperResponse from(ExamPaper paper) {
        return ExamPaperResponse.builder()
                .id(paper.getId())
                .title(paper.getTitle())
                .examType(paper.getExamType())
                .pdfUrl(paper.getPdfUrl())
                .totalQuestions(paper.getTotalQuestions())
                .timeLimit(paper.getTimeLimit())
                .description(paper.getDescription())
                .createdAt(paper.getCreatedAt())
                .build();
    }
}

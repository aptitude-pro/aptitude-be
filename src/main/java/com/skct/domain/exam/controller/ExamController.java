package com.skct.domain.exam.controller;

import com.skct.domain.exam.dto.ExamPaperResponse;
import com.skct.domain.exam.service.ExamService;
import com.skct.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Exam", description = "시험지 API")
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @Operation(summary = "시험지 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExamPaperResponse>>> getExamList(
            @RequestParam(required = false) String type,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getExamList(type, pageable)));
    }

    @Operation(summary = "시험지 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamPaperResponse>> getExam(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getExam(id)));
    }

    @Operation(summary = "시험지 생성 (메타 정보 + 정답)")
    @PostMapping
    public ResponseEntity<ApiResponse<ExamPaperResponse>> createExam(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateExamRequest request) {
        ExamPaperResponse result = examService.createExam(
                userId,
                request.getTitle(),
                request.getExamType(),
                request.getTotalQuestions(),
                request.getTimeLimit(),
                request.getDescription(),
                request.getPdfUrl(),
                request.getQuestions()
        );
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Getter
    static class CreateExamRequest {
        private String title;
        private String examType;
        private Integer totalQuestions;
        private Integer timeLimit;
        private String description;
        private String pdfUrl;
        private List<Map<String, Object>> questions;
    }
}

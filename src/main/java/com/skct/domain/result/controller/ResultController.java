package com.skct.domain.result.controller;

import com.skct.domain.result.dto.ExamResultResponse;
import com.skct.domain.result.dto.ManualResultRequest;
import com.skct.domain.result.service.ResultService;
import com.skct.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Tag(name = "Result", description = "성적 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @Operation(summary = "내 성적 목록")
    @GetMapping("/results")
    public ResponseEntity<ApiResponse<Page<ExamResultResponse>>> getResults(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.getResults(userId, pageable)));
    }

    @Operation(summary = "성적 상세")
    @GetMapping("/results/{id}")
    public ResponseEntity<ApiResponse<ExamResultResponse>> getResult(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.getResult(id, userId)));
    }

    @Operation(summary = "점수 성장 데이터")
    @GetMapping("/stats/growth")
    public ResponseEntity<ApiResponse<List<ResultService.GrowthDataPoint>>> getGrowthData(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String examType) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.getGrowthData(userId, examType)));
    }

    @Operation(summary = "영역별 통계")
    @GetMapping("/stats/category")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getCategoryData(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.getCategoryData(userId)));
    }

    @Operation(summary = "통계 요약")
    @GetMapping("/stats/summary")
    public ResponseEntity<ApiResponse<ResultService.StatsSummary>> getStatsSummary(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.getStatsSummary(userId)));
    }

    @Operation(summary = "성적 삭제")
    @DeleteMapping("/results/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResult(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        resultService.deleteResult(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "수동 채점 결과 저장 (SKCT)")
    @PostMapping("/results/manual")
    public ResponseEntity<ApiResponse<ExamResultResponse>> createManualResult(
            @AuthenticationPrincipal Long userId,
            @RequestBody ManualResultRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(resultService.createManualResult(userId, request)));
    }

    @Operation(summary = "수동 채점 결과 업데이트 (임시저장 → 최종)")
    @PutMapping("/results/{id}/manual")
    public ResponseEntity<ApiResponse<ExamResultResponse>> updateManualResult(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody ManualResultRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.updateManualResult(userId, id, request)));
    }
}

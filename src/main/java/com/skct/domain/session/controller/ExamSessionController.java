package com.skct.domain.session.controller;

import com.skct.domain.result.dto.ExamResultResponse;
import com.skct.domain.result.entity.ExamResult;
import com.skct.domain.session.service.ExamSessionService;
import com.skct.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Session", description = "시험 세션 API")
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ExamSessionController {

    private final ExamSessionService sessionService;

    @Operation(summary = "시험 세션 시작")
    @PostMapping
    public ResponseEntity<ApiResponse<ExamSessionService.SessionResponse>> startSession(
            @AuthenticationPrincipal Long userId,
            @RequestBody StartSessionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionService.startSession(userId, request.getExamPaperId())));
    }

    @Operation(summary = "세션 상태 조회 (재접속 복구)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamSessionService.SessionResponse>> getSession(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getSession(id, userId)));
    }

    @Operation(summary = "답안 저장")
    @PutMapping("/{id}/answers")
    public ResponseEntity<ApiResponse<Void>> saveAnswers(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody SaveAnswersRequest request) {
        sessionService.saveAnswers(id, userId, request.getAnswers());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "메모 저장")
    @PutMapping("/{id}/memo")
    public ResponseEntity<ApiResponse<Void>> saveMemo(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody Map<String, Object> memoData) {
        sessionService.saveMemo(id, userId, memoData);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "시험 제출 & 채점")
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<ExamResultResponse>> submit(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        ExamResult result = sessionService.submitSession(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(ExamResultResponse.from(result, null)));
    }

    @Operation(summary = "시험 세션 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        sessionService.deleteSession(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Getter
    static class StartSessionRequest {
        private Long examPaperId;
    }

    @Getter
    static class SaveAnswersRequest {
        private List<Map<String, Object>> answers;
    }
}

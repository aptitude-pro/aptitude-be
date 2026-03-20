package com.skct.domain.my.controller;

import com.skct.domain.my.service.MyStudyService;
import com.skct.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "My Study", description = "개인 학습 기록 API")
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MyStudyController {

    private final MyStudyService myStudyService;

    // ─── 개인 책 ───

    @Operation(summary = "내 책 목록 조회")
    @GetMapping("/books")
    public ResponseEntity<ApiResponse<List<MyStudyService.MyBookDto>>> getMyBooks(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(myStudyService.getMyBooks(userId)));
    }

    @Operation(summary = "내 책 등록")
    @PostMapping("/books")
    public ResponseEntity<ApiResponse<MyStudyService.MyBookDto>> addMyBook(
            @AuthenticationPrincipal Long userId,
            @RequestBody BookRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                myStudyService.addMyBook(userId, req.getTitle(), req.getYear(), req.getExamType())));
    }

    @Operation(summary = "내 책 삭제")
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<Void>> deleteMyBook(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long bookId) {
        myStudyService.deleteMyBook(userId, bookId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ─── 개인 학습 기록 ───

    @Operation(summary = "내 학습 기록 월별 조회")
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<MyStudyService.MyLogDto>>> getMyLogs(
            @AuthenticationPrincipal Long userId,
            @RequestParam String month) {
        return ResponseEntity.ok(ApiResponse.ok(myStudyService.getMyLogs(userId, month)));
    }

    @Operation(summary = "내 학습 기록 저장/수정")
    @PostMapping("/logs")
    public ResponseEntity<ApiResponse<MyStudyService.MyLogDto>> upsertMyLog(
            @AuthenticationPrincipal Long userId,
            @RequestBody LogRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                myStudyService.upsertMyLog(userId, req.getLogDate(), req.getBookId(),
                        req.getMemo(), req.getCategories())));
    }

    @Operation(summary = "내 학습 기록 삭제")
    @DeleteMapping("/logs/{logId}")
    public ResponseEntity<ApiResponse<Void>> deleteMyLog(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long logId) {
        myStudyService.deleteMyLog(userId, logId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ─── 스터디 학습 기록 연계 ───

    @Operation(summary = "내가 속한 스터디의 월별 학습 기록 조회")
    @GetMapping("/study-logs")
    public ResponseEntity<ApiResponse<List<MyStudyService.StudyLogSummaryDto>>> getMyStudyLogs(
            @AuthenticationPrincipal Long userId,
            @RequestParam String month) {
        return ResponseEntity.ok(ApiResponse.ok(myStudyService.getMyStudyLogs(userId, month)));
    }

    // ─── Request ───

    @Getter
    public static class BookRequest {
        private String title;
        private Integer year;
        private String examType;
    }

    @Getter
    public static class LogRequest {
        private LocalDate logDate;
        private Long bookId;
        private String memo;
        private List<MyStudyService.CategoryInput> categories;
    }
}

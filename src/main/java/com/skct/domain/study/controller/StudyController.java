package com.skct.domain.study.controller;

import com.skct.domain.study.service.StudyService;
import com.skct.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Study", description = "스터디 API")
@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @Operation(summary = "내 스터디 목록 / 공개 스터디 탐색")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudyService.StudyResponse>>> getStudies(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) String keyword) {
        List<StudyService.StudyResponse> result = (Boolean.TRUE.equals(isPublic))
                ? studyService.getPublicStudies(keyword, userId)
                : studyService.getMyStudies(userId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Operation(summary = "스터디 상세")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudyService.StudyDetailResponse>> getStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getStudy(id, userId)));
    }

    @Operation(summary = "스터디 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<StudyService.StudyResponse>> createStudy(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateStudyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                studyService.createStudy(userId, request.getName(), request.getExamType(),
                        request.getMaxMembers(), request.isPublic())));
    }

    @Operation(summary = "초대코드로 참가")
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<StudyService.StudyResponse>> joinStudy(
            @AuthenticationPrincipal Long userId,
            @RequestBody JoinStudyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.joinStudy(userId, request.getInviteCode())));
    }

    @Operation(summary = "공개 스터디 직접 참가")
    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<StudyService.StudyResponse>> joinPublicStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.joinPublicStudy(id, userId)));
    }

    @Operation(summary = "스터디 랭킹")
    @GetMapping("/{id}/ranking")
    public ResponseEntity<ApiResponse<List<StudyService.RankingDto>>> getRanking(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getRanking(id)));
    }

    @Operation(summary = "스터디 삭제 (리더만)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        studyService.deleteStudy(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "스터디 탈퇴")
    @DeleteMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        studyService.leaveStudy(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "멤버 강제 탈퇴 (리더만)")
    @DeleteMapping("/{id}/members/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> kickMember(
            @PathVariable Long id,
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal Long userId) {
        studyService.kickMember(id, userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "스터디 대시보드")
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<ApiResponse<StudyService.StudyDashboardResponse>> getDashboard(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getDashboard(id, userId)));
    }

    @Operation(summary = "공지 목록")
    @GetMapping("/{id}/notices")
    public ResponseEntity<ApiResponse<List<StudyService.NoticeDto>>> getNotices(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getNotices(id)));
    }

    @Operation(summary = "공지 등록 (리더만)")
    @PostMapping("/{id}/notices")
    public ResponseEntity<ApiResponse<StudyService.NoticeDto>> createNotice(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                studyService.createNotice(id, userId, request.getTitle(), request.getContent())));
    }

    @Operation(summary = "공지 삭제 (리더만)")
    @DeleteMapping("/{id}/notices/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long id,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal Long userId) {
        studyService.deleteNotice(id, userId, noticeId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ────────────────── Books ──────────────────

    @Operation(summary = "스터디 책 목록")
    @GetMapping("/{id}/books")
    public ResponseEntity<ApiResponse<List<StudyService.StudyBookDto>>> getBooks(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getBooks(id)));
    }

    @Operation(summary = "책 등록 (멤버 누구나)")
    @PostMapping("/{id}/books")
    public ResponseEntity<ApiResponse<StudyService.StudyBookDto>> addBook(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody BookRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                studyService.addBook(id, userId, request.getTitle(), request.getAuthor(),
                        request.getPublisher(), request.getIsbn())));
    }

    @Operation(summary = "책 삭제 (등록자 or 리더)")
    @DeleteMapping("/{id}/books/{bookId}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(
            @PathVariable Long id,
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long userId) {
        studyService.deleteBook(id, userId, bookId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ────────────────── StudyLog ──────────────────

    @Operation(summary = "내 학습 기록 월별 조회")
    @GetMapping("/{id}/logs")
    public ResponseEntity<ApiResponse<List<StudyService.StudyLogDto>>> getMyLogs(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestParam String month) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getMyLogs(id, userId, month)));
    }

    @Operation(summary = "학습 기록 생성/수정 (upsert)")
    @PostMapping("/{id}/logs")
    public ResponseEntity<ApiResponse<StudyService.StudyLogDto>> upsertLog(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody UpsertLogRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                studyService.upsertLog(id, userId, request.getLogDate(),
                        request.getBookId(), request.getMemo(), request.getCategories())));
    }

    @Operation(summary = "학습 기록 삭제")
    @DeleteMapping("/{id}/logs/{logId}")
    public ResponseEntity<ApiResponse<Void>> deleteLog(
            @PathVariable Long id,
            @PathVariable Long logId,
            @AuthenticationPrincipal Long userId) {
        studyService.deleteLog(id, userId, logId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "스터디원 월별 학습 기록 전체 조회")
    @GetMapping("/{id}/logs/members")
    public ResponseEntity<ApiResponse<List<StudyService.MemberLogDto>>> getMemberLogs(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestParam String month) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getMemberLogs(id, userId, month)));
    }

    @Operation(summary = "스터디원 오늘 학습 현황")
    @GetMapping("/{id}/logs/today-summary")
    public ResponseEntity<ApiResponse<List<StudyService.MemberTodayDto>>> getTodaySummary(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(studyService.getTodaySummary(id, userId)));
    }

    @Getter static class CreateStudyRequest {
        private String name;
        private String examType;
        private Integer maxMembers;
        @JsonProperty("isPublic")
        private boolean isPublic;
    }

    @Getter static class JoinStudyRequest {
        private String inviteCode;
    }

    @Getter static class NoticeRequest {
        private String title;
        private String content;
    }

    @Getter static class BookRequest {
        private String title;
        private String author;
        private String publisher;
        private String isbn;
    }

    @Getter static class UpsertLogRequest {
        private LocalDate logDate;
        private Long bookId;
        private String memo;
        private List<StudyService.CategoryInput> categories;
    }
}

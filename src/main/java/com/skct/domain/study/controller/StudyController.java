package com.skct.domain.study.controller;

import com.skct.domain.study.service.StudyService;
import com.skct.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(ApiResponse.ok(studyService.getDashboard(id)));
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

    @Getter static class CreateStudyRequest {
        private String name;
        private String examType;
        private Integer maxMembers;
        private boolean isPublic;
    }

    @Getter static class JoinStudyRequest {
        private String inviteCode;
    }

    @Getter static class NoticeRequest {
        private String title;
        private String content;
    }
}

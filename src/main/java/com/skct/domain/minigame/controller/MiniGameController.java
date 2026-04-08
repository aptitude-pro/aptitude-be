package com.skct.domain.minigame.controller;

import com.skct.domain.minigame.dto.MiniGameRankingEntry;
import com.skct.domain.minigame.dto.MiniGameScoreRequest;
import com.skct.domain.minigame.dto.MiniGameScoreResponse;
import com.skct.domain.minigame.service.MiniGameService;
import com.skct.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MiniGame", description = "자료해석 미니게임 API")
@RestController
@RequestMapping("/api/mini-game")
@RequiredArgsConstructor
public class MiniGameController {

    private final MiniGameService miniGameService;

    @Operation(summary = "점수 제출 (로그인 필요)")
    @PostMapping("/scores")
    public ResponseEntity<ApiResponse<MiniGameScoreResponse>> submitScore(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody MiniGameScoreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(miniGameService.saveScore(userId, request)));
    }

    @Operation(summary = "전국 랭킹 조회 (공개)")
    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<MiniGameRankingEntry>>> getRanking() {
        return ResponseEntity.ok(ApiResponse.ok(miniGameService.getRanking()));
    }
}

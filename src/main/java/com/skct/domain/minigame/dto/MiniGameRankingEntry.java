package com.skct.domain.minigame.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MiniGameRankingEntry {
    private Long userId;
    private String nickname;
    private Integer bestScore;
    private LocalDateTime achievedAt;
}

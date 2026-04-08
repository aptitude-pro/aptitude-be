package com.skct.domain.minigame.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MiniGameScoreResponse {
    private Integer rank;
    private boolean isNewBest;
    private Integer previousBest;
}

package com.skct.domain.minigame.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MiniGameScoreRequest {

    @NotNull
    @Min(0)
    private Integer score;

    @NotNull
    @Min(0)
    private Integer correctCount;

    @NotNull
    @Min(0)
    private Integer totalCount;
}

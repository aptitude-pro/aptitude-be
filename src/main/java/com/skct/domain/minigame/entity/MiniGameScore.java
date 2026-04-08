package com.skct.domain.minigame.entity;

import com.skct.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "mini_game_scores",
    indexes = @Index(name = "idx_minigame_user_score", columnList = "userId, score DESC")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class MiniGameScore extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // 합계는 최대 ~5억 (2~8자리 숫자 × 20셀) → Integer 범위(약 21억) 안전
    // 단, 표시 점수(게임 점수)는 최대 수백 점 수준으로 Integer 충분
    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer correctCount;

    @Column(nullable = false)
    private Integer totalCount;
}

package com.skct.domain.minigame.service;

import com.skct.domain.minigame.dto.MiniGameRankingEntry;
import com.skct.domain.minigame.dto.MiniGameScoreRequest;
import com.skct.domain.minigame.dto.MiniGameScoreResponse;
import com.skct.domain.minigame.entity.MiniGameScore;
import com.skct.domain.minigame.repository.MiniGameScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MiniGameService {

    private final MiniGameScoreRepository scoreRepository;

    @Transactional
    public MiniGameScoreResponse saveScore(Long userId, MiniGameScoreRequest request) {
        // 기존 최고점 조회
        int previousBest = scoreRepository.findTopByUserIdOrderByScoreDesc(userId)
                .map(MiniGameScore::getScore)
                .orElse(0);

        boolean isNewBest = request.getScore() > previousBest;

        // 점수 저장 (매 게임 기록, 최고점 갱신 여부와 무관)
        scoreRepository.save(MiniGameScore.builder()
                .userId(userId)
                .score(request.getScore())
                .correctCount(request.getCorrectCount())
                .totalCount(request.getTotalCount())
                .build());

        // 순위 = 나보다 높은 최고점 보유 유저 수 + 1
        int myBest = isNewBest ? request.getScore() : previousBest;
        long higherCount = scoreRepository.countUsersWithHigherBest(myBest);
        int rank = (int) higherCount + 1;

        return MiniGameScoreResponse.builder()
                .rank(rank)
                .isNewBest(isNewBest)
                .previousBest(previousBest)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MiniGameRankingEntry> getRanking() {
        return scoreRepository.findTopRanking(PageRequest.of(0, 50));
    }
}

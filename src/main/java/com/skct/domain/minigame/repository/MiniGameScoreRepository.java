package com.skct.domain.minigame.repository;

import com.skct.domain.minigame.dto.MiniGameRankingEntry;
import com.skct.domain.minigame.entity.MiniGameScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MiniGameScoreRepository extends JpaRepository<MiniGameScore, Long> {

    Optional<MiniGameScore> findTopByUserIdOrderByScoreDesc(Long userId);

    /**
     * 유저별 최고점 TOP N
     * Pageable(0, 50)으로 호출하면 TOP 50
     */
    @Query("""
        SELECT new com.skct.domain.minigame.dto.MiniGameRankingEntry(
            m.userId, u.nickname, MAX(m.score), MAX(m.createdAt)
        )
        FROM MiniGameScore m, com.skct.domain.user.entity.User u
        WHERE u.id = m.userId
        GROUP BY m.userId, u.nickname
        ORDER BY MAX(m.score) DESC
        """)
    List<MiniGameRankingEntry> findTopRanking(Pageable pageable);

    /**
     * 특정 점수보다 높은 최고점을 가진 유저 수 (순위 계산)
     * MySQL 서브쿼리 사용 (nativeQuery)
     */
    @Query(value = """
        SELECT COUNT(*) FROM (
            SELECT user_id, MAX(score) AS best
            FROM mini_game_scores
            GROUP BY user_id
            HAVING MAX(score) > :score
        ) t
        """, nativeQuery = true)
    long countUsersWithHigherBest(@Param("score") Integer score);
}

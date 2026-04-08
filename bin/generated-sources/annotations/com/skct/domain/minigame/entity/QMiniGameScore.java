package com.skct.domain.minigame.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMiniGameScore is a Querydsl query type for MiniGameScore
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMiniGameScore extends EntityPathBase<MiniGameScore> {

    private static final long serialVersionUID = 748733041L;

    public static final QMiniGameScore miniGameScore = new QMiniGameScore("miniGameScore");

    public final com.skct.global.common.QBaseTimeEntity _super = new com.skct.global.common.QBaseTimeEntity(this);

    public final NumberPath<Integer> correctCount = createNumber("correctCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    public final NumberPath<Integer> totalCount = createNumber("totalCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QMiniGameScore(String variable) {
        super(MiniGameScore.class, forVariable(variable));
    }

    public QMiniGameScore(Path<? extends MiniGameScore> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMiniGameScore(PathMetadata metadata) {
        super(MiniGameScore.class, metadata);
    }

}


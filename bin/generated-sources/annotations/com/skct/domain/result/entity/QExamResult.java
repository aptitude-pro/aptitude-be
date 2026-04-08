package com.skct.domain.result.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExamResult is a Querydsl query type for ExamResult
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExamResult extends EntityPathBase<ExamResult> {

    private static final long serialVersionUID = -910484056L;

    public static final QExamResult examResult = new QExamResult("examResult");

    public final com.skct.global.common.QBaseTimeEntity _super = new com.skct.global.common.QBaseTimeEntity(this);

    public final MapPath<String, Integer, NumberPath<Integer>> categoryScores = this.<String, Integer, NumberPath<Integer>>createMap("categoryScores", String.class, Integer.class, NumberPath.class);

    public final NumberPath<Integer> correctCount = createNumber("correctCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> elapsedSeconds = createNumber("elapsedSeconds", Integer.class);

    public final NumberPath<Long> examPaperId = createNumber("examPaperId", Long.class);

    public final StringPath examPeriod = createString("examPeriod");

    public final StringPath examRound = createString("examRound");

    public final StringPath examTitle = createString("examTitle");

    public final StringPath examType = createString("examType");

    public final NumberPath<Integer> examYear = createNumber("examYear", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDraft = createBoolean("isDraft");

    public final StringPath platform = createString("platform");

    public final NumberPath<Long> sessionId = createNumber("sessionId", Long.class);

    public final NumberPath<Integer> totalCount = createNumber("totalCount", Integer.class);

    public final NumberPath<Integer> totalScore = createNumber("totalScore", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QExamResult(String variable) {
        super(ExamResult.class, forVariable(variable));
    }

    public QExamResult(Path<? extends ExamResult> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExamResult(PathMetadata metadata) {
        super(ExamResult.class, metadata);
    }

}


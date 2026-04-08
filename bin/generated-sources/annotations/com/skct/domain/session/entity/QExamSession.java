package com.skct.domain.session.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExamSession is a Querydsl query type for ExamSession
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExamSession extends EntityPathBase<ExamSession> {

    private static final long serialVersionUID = -479792458L;

    public static final QExamSession examSession = new QExamSession("examSession");

    public final com.skct.global.common.QBaseTimeEntity _super = new com.skct.global.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> elapsedSeconds = createNumber("elapsedSeconds", Integer.class);

    public final NumberPath<Long> examPaperId = createNumber("examPaperId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> finishedAt = createDateTime("finishedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final MapPath<String, Object, SimplePath<Object>> memoData = this.<String, Object, SimplePath<Object>>createMap("memoData", String.class, Object.class, SimplePath.class);

    public final DateTimePath<java.time.LocalDateTime> startedAt = createDateTime("startedAt", java.time.LocalDateTime.class);

    public final EnumPath<ExamSession.Status> status = createEnum("status", ExamSession.Status.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QExamSession(String variable) {
        super(ExamSession.class, forVariable(variable));
    }

    public QExamSession(Path<? extends ExamSession> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExamSession(PathMetadata metadata) {
        super(ExamSession.class, metadata);
    }

}


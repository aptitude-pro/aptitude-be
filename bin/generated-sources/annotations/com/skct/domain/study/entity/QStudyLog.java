package com.skct.domain.study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudyLog is a Querydsl query type for StudyLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyLog extends EntityPathBase<StudyLog> {

    private static final long serialVersionUID = 309785743L;

    public static final QStudyLog studyLog = new QStudyLog("studyLog");

    public final com.skct.global.common.QBaseTimeEntity _super = new com.skct.global.common.QBaseTimeEntity(this);

    public final NumberPath<Long> bookId = createNumber("bookId", Long.class);

    public final ListPath<StudyLogCategory, QStudyLogCategory> categories = this.<StudyLogCategory, QStudyLogCategory>createList("categories", StudyLogCategory.class, QStudyLogCategory.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> logDate = createDate("logDate", java.time.LocalDate.class);

    public final StringPath memo = createString("memo");

    public final NumberPath<Long> studyId = createNumber("studyId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QStudyLog(String variable) {
        super(StudyLog.class, forVariable(variable));
    }

    public QStudyLog(Path<? extends StudyLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudyLog(PathMetadata metadata) {
        super(StudyLog.class, metadata);
    }

}


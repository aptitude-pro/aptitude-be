package com.skct.domain.study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStudyNotice is a Querydsl query type for StudyNotice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyNotice extends EntityPathBase<StudyNotice> {

    private static final long serialVersionUID = -999899731L;

    public static final QStudyNotice studyNotice = new QStudyNotice("studyNotice");

    public final com.skct.global.common.QBaseTimeEntity _super = new com.skct.global.common.QBaseTimeEntity(this);

    public final NumberPath<Long> authorId = createNumber("authorId", Long.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> studyId = createNumber("studyId", Long.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QStudyNotice(String variable) {
        super(StudyNotice.class, forVariable(variable));
    }

    public QStudyNotice(Path<? extends StudyNotice> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudyNotice(PathMetadata metadata) {
        super(StudyNotice.class, metadata);
    }

}


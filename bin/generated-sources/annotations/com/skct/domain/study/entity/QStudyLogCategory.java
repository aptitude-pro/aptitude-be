package com.skct.domain.study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudyLogCategory is a Querydsl query type for StudyLogCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyLogCategory extends EntityPathBase<StudyLogCategory> {

    private static final long serialVersionUID = 1331304109L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStudyLogCategory studyLogCategory = new QStudyLogCategory("studyLogCategory");

    public final StringPath categoryName = createString("categoryName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> problemCount = createNumber("problemCount", Integer.class);

    public final QStudyLog studyLog;

    public QStudyLogCategory(String variable) {
        this(StudyLogCategory.class, forVariable(variable), INITS);
    }

    public QStudyLogCategory(Path<? extends StudyLogCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStudyLogCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStudyLogCategory(PathMetadata metadata, PathInits inits) {
        this(StudyLogCategory.class, metadata, inits);
    }

    public QStudyLogCategory(Class<? extends StudyLogCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.studyLog = inits.isInitialized("studyLog") ? new QStudyLog(forProperty("studyLog")) : null;
    }

}


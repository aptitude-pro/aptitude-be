package com.skct.domain.study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStudyMember is a Querydsl query type for StudyMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyMember extends EntityPathBase<StudyMember> {

    private static final long serialVersionUID = -1037979281L;

    public static final QStudyMember studyMember = new QStudyMember("studyMember");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> joinedAt = createDateTime("joinedAt", java.time.LocalDateTime.class);

    public final EnumPath<StudyMember.Role> role = createEnum("role", StudyMember.Role.class);

    public final NumberPath<Long> studyId = createNumber("studyId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QStudyMember(String variable) {
        super(StudyMember.class, forVariable(variable));
    }

    public QStudyMember(Path<? extends StudyMember> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudyMember(PathMetadata metadata) {
        super(StudyMember.class, metadata);
    }

}


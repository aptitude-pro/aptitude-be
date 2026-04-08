package com.skct.domain.study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStudyBook is a Querydsl query type for StudyBook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyBook extends EntityPathBase<StudyBook> {

    private static final long serialVersionUID = 1013125886L;

    public static final QStudyBook studyBook = new QStudyBook("studyBook");

    public final com.skct.global.common.QBaseTimeEntity _super = new com.skct.global.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath examType = createString("examType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> registeredBy = createNumber("registeredBy", Long.class);

    public final NumberPath<Long> studyId = createNumber("studyId", Long.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QStudyBook(String variable) {
        super(StudyBook.class, forVariable(variable));
    }

    public QStudyBook(Path<? extends StudyBook> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudyBook(PathMetadata metadata) {
        super(StudyBook.class, metadata);
    }

}


package com.skct.domain.exam.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExamPaper is a Querydsl query type for ExamPaper
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExamPaper extends EntityPathBase<ExamPaper> {

    private static final long serialVersionUID = -424507809L;

    public static final QExamPaper examPaper = new QExamPaper("examPaper");

    public final com.skct.global.common.QBaseTimeEntity _super = new com.skct.global.common.QBaseTimeEntity(this);

    public final BooleanPath active = createBoolean("active");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final StringPath examType = createString("examType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath pdfUrl = createString("pdfUrl");

    public final NumberPath<Integer> timeLimit = createNumber("timeLimit", Integer.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> totalQuestions = createNumber("totalQuestions", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> uploadedBy = createNumber("uploadedBy", Long.class);

    public QExamPaper(String variable) {
        super(ExamPaper.class, forVariable(variable));
    }

    public QExamPaper(Path<? extends ExamPaper> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExamPaper(PathMetadata metadata) {
        super(ExamPaper.class, metadata);
    }

}


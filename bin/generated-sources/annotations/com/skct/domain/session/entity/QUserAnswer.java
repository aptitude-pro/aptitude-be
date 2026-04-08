package com.skct.domain.session.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserAnswer is a Querydsl query type for UserAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAnswer extends EntityPathBase<UserAnswer> {

    private static final long serialVersionUID = -816181686L;

    public static final QUserAnswer userAnswer = new QUserAnswer("userAnswer");

    public final NumberPath<Long> examResultId = createNumber("examResultId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isGuessed = createBoolean("isGuessed");

    public final BooleanPath isWrong = createBoolean("isWrong");

    public final NumberPath<Integer> questionNo = createNumber("questionNo", Integer.class);

    public final NumberPath<Integer> selectedAnswer = createNumber("selectedAnswer", Integer.class);

    public final NumberPath<Long> sessionId = createNumber("sessionId", Long.class);

    public QUserAnswer(String variable) {
        super(UserAnswer.class, forVariable(variable));
    }

    public QUserAnswer(Path<? extends UserAnswer> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserAnswer(PathMetadata metadata) {
        super(UserAnswer.class, metadata);
    }

}


package com.skct.domain.my.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMyBook is a Querydsl query type for MyBook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMyBook extends EntityPathBase<MyBook> {

    private static final long serialVersionUID = -1177917040L;

    public static final QMyBook myBook = new QMyBook("myBook");

    public final com.skct.global.common.QBaseTimeEntity _super = new com.skct.global.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath examType = createString("examType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QMyBook(String variable) {
        super(MyBook.class, forVariable(variable));
    }

    public QMyBook(Path<? extends MyBook> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMyBook(PathMetadata metadata) {
        super(MyBook.class, metadata);
    }

}


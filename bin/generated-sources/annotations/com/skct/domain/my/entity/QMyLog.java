package com.skct.domain.my.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMyLog is a Querydsl query type for MyLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMyLog extends EntityPathBase<MyLog> {

    private static final long serialVersionUID = -1839103043L;

    public static final QMyLog myLog = new QMyLog("myLog");

    public final com.skct.global.common.QBaseTimeEntity _super = new com.skct.global.common.QBaseTimeEntity(this);

    public final NumberPath<Long> bookId = createNumber("bookId", Long.class);

    public final ListPath<MyLogCategory, QMyLogCategory> categories = this.<MyLogCategory, QMyLogCategory>createList("categories", MyLogCategory.class, QMyLogCategory.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> logDate = createDate("logDate", java.time.LocalDate.class);

    public final StringPath memo = createString("memo");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QMyLog(String variable) {
        super(MyLog.class, forVariable(variable));
    }

    public QMyLog(Path<? extends MyLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMyLog(PathMetadata metadata) {
        super(MyLog.class, metadata);
    }

}


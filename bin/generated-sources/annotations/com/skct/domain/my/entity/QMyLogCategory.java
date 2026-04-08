package com.skct.domain.my.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMyLogCategory is a Querydsl query type for MyLogCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMyLogCategory extends EntityPathBase<MyLogCategory> {

    private static final long serialVersionUID = 596744155L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMyLogCategory myLogCategory = new QMyLogCategory("myLogCategory");

    public final StringPath categoryName = createString("categoryName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMyLog myLog;

    public final NumberPath<Integer> problemCount = createNumber("problemCount", Integer.class);

    public QMyLogCategory(String variable) {
        this(MyLogCategory.class, forVariable(variable), INITS);
    }

    public QMyLogCategory(Path<? extends MyLogCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMyLogCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMyLogCategory(PathMetadata metadata, PathInits inits) {
        this(MyLogCategory.class, metadata, inits);
    }

    public QMyLogCategory(Class<? extends MyLogCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.myLog = inits.isInitialized("myLog") ? new QMyLog(forProperty("myLog")) : null;
    }

}


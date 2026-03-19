package com.skct.domain.study.entity;

import com.skct.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "study_books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class StudyBook extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studyId;

    @Column(nullable = false)
    private String title;

    @Column
    private String author;

    @Column
    private String publisher;

    @Column(length = 20)
    private String isbn;

    @Column(nullable = false)
    private Long registeredBy;
}

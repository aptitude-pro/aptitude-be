package com.skct.domain.exam.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long examPaperId;

    @Column(nullable = false)
    private Integer questionNo;

    @Column(nullable = false)
    private Integer correctAnswer;

    @Column(length = 20)
    private String category; // 언어이해, 수리논리, 추리, 시공간
}

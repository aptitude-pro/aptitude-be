package com.skct.domain.exam.entity;

import com.skct.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_papers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ExamPaper extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 20)
    private String examType;

    @Column
    private String pdfUrl;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Integer timeLimit; // 분

    @Column(nullable = false)
    private Long uploadedBy;

    @Column
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}

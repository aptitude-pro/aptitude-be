package com.skct.domain.study.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "study_log_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class StudyLogCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    private StudyLog studyLog;

    @Column(nullable = false, length = 50)
    private String categoryName;

    @Column(nullable = false)
    private int problemCount;
}

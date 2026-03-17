package com.skct.domain.result.entity;

import com.skct.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "exam_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ExamResult extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    private Long sessionId;

    @Column
    private Long examPaperId;

    @Column
    private Integer examYear;

    @Column(length = 10)
    private String examPeriod;

    @Column(length = 50)
    private String platform;

    @Column(length = 10)
    private String examRound;

    @Column(length = 20)
    private String examType;

    @Column
    private String examTitle;

    @Column(nullable = false)
    private Integer totalScore;

    @Column(nullable = false)
    private Integer correctCount;

    @Column(nullable = false)
    private Integer totalCount;

    @Column
    private Integer elapsedSeconds;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Integer> categoryScores;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDraft = false;
}

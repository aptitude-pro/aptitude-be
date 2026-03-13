package com.skct.domain.session.entity;

import com.skct.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "exam_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ExamSession extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long examPaperId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.IN_PROGRESS;

    @Column
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime finishedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> memoData;

    @Column
    private Integer elapsedSeconds;

    public void updateStatus(Status status) {
        this.status = status;
        if (status == Status.SUBMITTED || status == Status.EXPIRED) {
            this.finishedAt = LocalDateTime.now();
            if (this.startedAt != null) {
                this.elapsedSeconds = (int) java.time.Duration.between(startedAt, finishedAt).getSeconds();
            }
        }
    }

    public void updateMemoData(Map<String, Object> memoData) {
        this.memoData = memoData;
    }

    public enum Status {
        IN_PROGRESS, SUBMITTED, EXPIRED, COMPLETED
    }
}

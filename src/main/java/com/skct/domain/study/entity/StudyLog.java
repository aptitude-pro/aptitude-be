package com.skct.domain.study.entity;

import com.skct.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_logs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "study_id", "log_date"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class StudyLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @OneToMany(mappedBy = "studyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyLogCategory> categories = new ArrayList<>();

    public void update(Long bookId, String memo) {
        this.bookId = bookId;
        this.memo = memo;
    }

    public void clearCategories() {
        this.categories.clear();
    }
}

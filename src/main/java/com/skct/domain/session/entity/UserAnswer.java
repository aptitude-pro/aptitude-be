package com.skct.domain.session.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_answers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_id", "question_no"}),
        @UniqueConstraint(columnNames = {"exam_result_id", "question_no"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "exam_result_id")
    private Long examResultId;

    @Column(name = "question_no", nullable = false)
    private Integer questionNo;

    @Column
    private Integer selectedAnswer;

    @Column(name = "is_guessed", nullable = false)
    @Builder.Default
    private boolean isGuessed = false;

    @Column(name = "is_wrong", nullable = false)
    @Builder.Default
    private boolean isWrong = false;

    public void updateAnswer(Integer answer) {
        this.selectedAnswer = answer;
    }

    public UserAnswer withMarks(boolean guessed, boolean wrong) {
        return UserAnswer.builder()
                .id(this.id)
                .sessionId(this.sessionId)
                .examResultId(this.examResultId)
                .questionNo(this.questionNo)
                .selectedAnswer(this.selectedAnswer)
                .isGuessed(guessed)
                .isWrong(wrong)
                .build();
    }
}

package com.skct.domain.session.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_answers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_id", "question_no"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "question_no", nullable = false)
    private Integer questionNo;

    @Column(nullable = false)
    private Integer selectedAnswer;

    public void updateAnswer(Integer answer) {
        this.selectedAnswer = answer;
    }
}

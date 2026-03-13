package com.skct.domain.study.entity;

import com.skct.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "studies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Study extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 20)
    private String examType;

    @Column(nullable = false, unique = true, length = 6)
    private String inviteCode;

    @Column(nullable = false)
    private Integer maxMembers;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private Long createdBy;

    @Column
    private Integer memberCount;

    public void updateMemberCount(int count) {
        this.memberCount = count;
    }
}

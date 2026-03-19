package com.skct.domain.my.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "my_log_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class MyLogCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    private MyLog myLog;

    @Column(nullable = false, length = 50)
    private String categoryName;

    @Column(nullable = false)
    private int problemCount;
}

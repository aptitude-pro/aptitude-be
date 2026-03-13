package com.skct.domain.user.dto;

import com.skct.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuthResponse {
    private UserDto user;
    private String accessToken;

    @Getter
    @Builder
    public static class UserDto {
        private Long id;
        private String email;
        private String nickname;
        private String role;
        private String targetExam;
        private LocalDateTime createdAt;
    }

    public static AuthResponse from(User user, String accessToken) {
        return AuthResponse.builder()
                .user(UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .role(user.getRole().name())
                        .targetExam(user.getTargetExam())
                        .createdAt(user.getCreatedAt())
                        .build())
                .accessToken(accessToken)
                .build();
    }
}

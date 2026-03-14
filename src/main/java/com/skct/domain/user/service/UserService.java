package com.skct.domain.user.service;

import com.skct.domain.result.repository.ExamResultRepository;
import com.skct.domain.session.repository.ExamSessionRepository;
import com.skct.domain.study.repository.StudyMemberRepository;
import com.skct.domain.user.dto.AuthResponse;
import com.skct.domain.user.entity.User;
import com.skct.domain.user.repository.UserRepository;
import com.skct.global.exception.CustomException;
import com.skct.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExamResultRepository resultRepository;
    private final ExamSessionRepository sessionRepository;
    private final StudyMemberRepository memberRepository;

    public AuthResponse.UserDto getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return toDto(user);
    }

    @Transactional
    public AuthResponse.UserDto updateProfile(Long userId, String nickname, String targetExam) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.updateProfile(nickname, targetExam);
        return toDto(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void deleteAccount(Long userId) {
        resultRepository.deleteByUserId(userId);
        sessionRepository.deleteByUserId(userId);
        memberRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    private AuthResponse.UserDto toDto(User user) {
        return AuthResponse.UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .targetExam(user.getTargetExam())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

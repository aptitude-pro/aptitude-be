package com.skct.domain.user.controller;

import com.skct.domain.user.dto.AuthResponse;
import com.skct.domain.user.dto.EmailCodeRequest;
import com.skct.domain.user.dto.EmailVerifyRequest;
import com.skct.domain.user.dto.LoginRequest;
import com.skct.domain.user.dto.RegisterRequest;
import com.skct.domain.user.service.AuthService;
import com.skct.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.secure-cookie:false}")
    private boolean secureCookie;

    @Operation(summary = "이메일 인증코드 발송")
    @PostMapping("/email/send-code")
    public ResponseEntity<ApiResponse<Void>> sendEmailCode(
            @Valid @RequestBody EmailCodeRequest request) throws MessagingException {
        authService.sendEmailCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "이메일 인증코드 확인")
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmailCode(
            @Valid @RequestBody EmailVerifyRequest request) {
        authService.verifyEmailCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        AuthResponse result = authService.register(request);
        setRefreshTokenCookie(response, result);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponse result = authService.login(request);
        setRefreshTokenCookie(response, result);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal Long userId,
            HttpServletResponse response) {
        authService.logout(userId);
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "토큰 갱신")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        AuthResponse result = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, AuthResponse result) {
        Cookie cookie = new Cookie("refreshToken", result.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(cookie);
    }
}

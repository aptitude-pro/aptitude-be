package com.skct.domain.user.service;

import com.skct.global.exception.CustomException;
import com.skct.global.exception.ErrorCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmailVerificationStore {

    private static final long CODE_TTL_MINUTES = 5;

    private final ConcurrentHashMap<String, VerificationEntry> store = new ConcurrentHashMap<>();

    public void save(String email, String code) {
        store.put(email, new VerificationEntry(code, LocalDateTime.now().plusMinutes(CODE_TTL_MINUTES)));
    }

    public void verify(String email, String code) {
        VerificationEntry entry = store.get(email);
        if (entry == null || entry.isExpired()) {
            throw new CustomException(ErrorCode.EMAIL_CODE_EXPIRED);
        }
        if (!entry.code().equals(code)) {
            throw new CustomException(ErrorCode.EMAIL_CODE_INVALID);
        }
        store.put(email, entry.markVerified());
    }

    public boolean isVerified(String email) {
        VerificationEntry entry = store.get(email);
        return entry != null && !entry.isExpired() && entry.verified();
    }

    public void remove(String email) {
        store.remove(email);
    }

    @Scheduled(fixedDelay = 60_000)
    public void evictExpired() {
        store.entrySet().removeIf(e -> e.getValue().isExpired());
    }

    private record VerificationEntry(String code, LocalDateTime expiresAt, boolean verified) {
        VerificationEntry(String code, LocalDateTime expiresAt) {
            this(code, expiresAt, false);
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }

        VerificationEntry markVerified() {
            return new VerificationEntry(code, expiresAt, true);
        }
    }
}

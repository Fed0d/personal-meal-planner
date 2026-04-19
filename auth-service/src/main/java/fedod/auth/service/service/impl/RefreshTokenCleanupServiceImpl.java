package fedod.auth.service.service.impl;

import fedod.auth.service.repository.RefreshTokenRepository;
import fedod.auth.service.service.RefreshTokenCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class RefreshTokenCleanupServiceImpl implements RefreshTokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public long cleanupExpiredTokens() {
        return refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}

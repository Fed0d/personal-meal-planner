package fedod.auth.service.service.scheduler;

import fedod.auth.service.service.RefreshTokenCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenCleanupService refreshTokenCleanupService;

    @Scheduled(cron = "${app.cleanup.refresh-token-cron}")
    public void cleanupExpiredRefreshTokens() {
        log.info("Starting scheduled cleanup of expired refresh tokens at {}", LocalDateTime.now());

        long deletedCount = refreshTokenCleanupService.cleanupExpiredTokens();

        log.info("Completed scheduled cleanup of expired refresh tokens. Deleted {} tokens.", deletedCount);
    }
}

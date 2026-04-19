package fedod.auth.service.service;

public interface RefreshTokenCleanupService {

    long cleanupExpiredTokens();
}

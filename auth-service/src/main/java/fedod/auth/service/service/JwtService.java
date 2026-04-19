package fedod.auth.service.service;

import fedod.auth.service.entity.AuthUser;

import java.util.UUID;

public interface JwtService {

    String generateAccessToken(AuthUser user);

    String generateRefreshToken();

    UUID extractUserId(String token);

    String extractEmail(String token);

    String extractRole(String token);

    boolean isTokenValid(String token);
}

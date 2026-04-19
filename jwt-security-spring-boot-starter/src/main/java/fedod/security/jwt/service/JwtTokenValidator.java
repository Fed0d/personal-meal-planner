package fedod.security.jwt.service;

import fedod.security.jwt.config.JwtSecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final RsaPublicKeyProvider rsaPublicKeyProvider;
    private final JwtSecurityProperties jwtSecurityProperties;

    public boolean isValid(String token) {
        try {
            log.info("Starting validation of JWT token");
            Claims claims = parseClaims(token);

            if (claims.getExpiration() == null || claims.getExpiration().before(new Date())) {
                return false;
            }

            String issuer = claims.getIssuer();

            return issuer != null && issuer.equals(jwtSecurityProperties.getIssuer());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(rsaPublicKeyProvider.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

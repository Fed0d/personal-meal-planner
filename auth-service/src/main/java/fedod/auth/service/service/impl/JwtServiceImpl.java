package fedod.auth.service.service.impl;

import fedod.auth.service.config.properties.JwtProperties;
import fedod.auth.service.entity.AuthUser;
import fedod.auth.service.service.JwtService;
import fedod.auth.service.service.security.RsaKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;
    private final RsaKeyProvider rsaKeyProvider;

    @Override
    public String generateAccessToken(AuthUser user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtProperties.getAccessTokenExpirationMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .signWith(rsaKeyProvider.getPrivateKey())
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID() + "." + UUID.randomUUID();
    }

    @Override
    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    @Override
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    @Override
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();

            return expiration != null && expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        PublicKey publicKey = rsaKeyProvider.getPublicKey();

        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

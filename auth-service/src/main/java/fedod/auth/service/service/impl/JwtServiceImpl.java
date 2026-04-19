package fedod.auth.service.service.impl;

import fedod.auth.service.config.properties.JwtProperties;
import fedod.auth.service.entity.AuthUser;
import fedod.auth.service.service.JwtService;
import fedod.auth.service.service.security.RsaPrivateKeyProvider;
import fedod.security.jwt.config.JwtSecurityProperties;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;
    private final JwtSecurityProperties jwtSecurityProperties;
    private final RsaPrivateKeyProvider rsaPrivateKeyProvider;

    @Override
    public String generateAccessToken(AuthUser user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtProperties.getAccessTokenExpirationMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(jwtSecurityProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .signWith(rsaPrivateKeyProvider.getPrivateKey())
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID() + "." + UUID.randomUUID();
    }
}

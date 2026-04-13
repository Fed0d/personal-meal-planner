package fedod.auth.service.repository;

import fedod.auth.service.entity.AuthUser;
import fedod.auth.service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUser(AuthUser user);

    void deleteAllByUser(AuthUser user);

    long countByUserAndRevokedFalse(AuthUser user);

    long deleteByExpiresAtBefore(LocalDateTime time);
}

package fedod.security.jwt.model;

import java.util.UUID;

public record JwtUserPrincipal(UUID userId, String email, String role) {
}

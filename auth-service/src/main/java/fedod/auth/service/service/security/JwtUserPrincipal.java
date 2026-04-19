package fedod.auth.service.service.security;

import java.util.UUID;

public record JwtUserPrincipal(UUID userId, String email, String role) {
}

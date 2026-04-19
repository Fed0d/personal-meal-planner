package fedod.auth.service.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MeResponse(UUID userId, String email, String role) {
}

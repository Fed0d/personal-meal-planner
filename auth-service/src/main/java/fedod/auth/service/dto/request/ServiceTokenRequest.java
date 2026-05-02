package fedod.auth.service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ServiceTokenRequest(@NotBlank String apiKey) {
}

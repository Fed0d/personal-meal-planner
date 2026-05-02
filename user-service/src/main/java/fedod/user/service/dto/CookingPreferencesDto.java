package fedod.user.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record CookingPreferencesDto(
        @Min(0) @Max(480) Integer activeCookingTimeMin,
        @Min(0) @Max(480) Integer passiveCookingTimeMin
) {
}

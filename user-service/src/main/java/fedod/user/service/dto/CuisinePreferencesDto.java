package fedod.user.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record CuisinePreferencesDto(
        @Min(0) @Max(10) Integer asian,
        @Min(0) @Max(10) Integer european,
        @Min(0) @Max(10) Integer eastern,
        @Min(0) @Max(10) Integer slavic,
        @Min(0) @Max(10) Integer american,
        @Min(0) @Max(10) Integer mexican
) {
}

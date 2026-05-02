package fedod.user.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record IngredientPreferenceDto(
        @NotBlank @Size(max = 100) String name,
        @NotNull @Min(0) @Max(10) Integer score
) {
}

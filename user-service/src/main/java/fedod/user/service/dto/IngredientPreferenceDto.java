package fedod.user.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record IngredientPreferenceDto(
        @Min(0) @Max(10) Integer fish,
        @Min(0) @Max(10) Integer seafood,
        @Min(0) @Max(10) Integer pork,
        @Min(0) @Max(10) Integer beef,
        @Min(0) @Max(10) Integer chicken,
        @Min(0) @Max(10) Integer cheese,
        @Min(0) @Max(10) Integer potato,
        @Min(0) @Max(10) Integer onion,
        @Min(0) @Max(10) Integer garlic,
        @Min(0) @Max(10) Integer tomatoes,
        @Min(0) @Max(10) Integer liver,
        @Min(0) @Max(10) Integer milk,
        @Min(0) @Max(10) Integer cottageCheese,
        @Min(0) @Max(10) Integer olives,
        @Min(0) @Max(10) Integer celery,
        @Min(0) @Max(10) Integer cilantro,
        @Min(0) @Max(10) Integer pumpkin,
        @Min(0) @Max(10) Integer eggplant,
        @Min(0) @Max(10) Integer nuts
) {
}

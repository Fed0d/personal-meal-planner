package fedod.user.service.dto;

import fedod.user.service.entity.enums.ActivityLevel;
import fedod.user.service.entity.enums.Gender;
import fedod.user.service.entity.enums.GoalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SubmitQuestionnaireRequest(
        @NotNull Gender gender,
        @NotNull @Past(message = "Birth date must be in the past") LocalDate birthDate,
        @NotNull @Min(50) @Max(300) Integer heightCm,
        @NotNull @DecimalMin("20.00") @DecimalMax("500.00") BigDecimal weightKg,
        @DecimalMin("20.00") @DecimalMax("500.00") BigDecimal targetWeightKg,
        @NotNull GoalType goalType,
        @NotNull ActivityLevel activityLevel,
        @NotNull @Min(0) @Max(480) Integer activeCookingTimeMin,
        @NotNull @Min(0) @Max(480) Integer passiveCookingTimeMin,
        @NotNull @Valid CuisinePreferencesDto cuisinePreferences,
        @NotNull List<@Valid IngredientPreferenceDto> ingredientPreferences,
        @NotNull @Valid AllergensDto allergens
) {
}

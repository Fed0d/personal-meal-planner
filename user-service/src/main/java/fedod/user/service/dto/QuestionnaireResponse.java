package fedod.user.service.dto;

import fedod.user.service.entity.enums.ActivityLevel;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record QuestionnaireResponse(
        ActivityLevel activityLevel,
        Integer activeCookingTimeMin,
        Integer passiveCookingTimeMin,
        BigDecimal bmr,
        BigDecimal targetCalories,
        CuisinePreferencesDto cuisinePreferences,
        IngredientPreferenceDto ingredientPreferences,
        AllergensDto allergens
) {
}

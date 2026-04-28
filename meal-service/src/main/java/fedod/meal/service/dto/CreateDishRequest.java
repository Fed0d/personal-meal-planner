package fedod.meal.service.dto;

import fedod.meal.service.entity.enums.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateDishRequest(
        @NotNull Long id,
        String url,
        @NotBlank @Size(max = 255) String title,
        String description,
        String dishImage,
        BigDecimal calories,
        BigDecimal protein,
        BigDecimal fat,
        BigDecimal carbs,
        String readyIn,
        String kitchenTime,
        String cuisine,
        String categoryPath,
        String recipe,
        @NotNull MealType mealType,
        List<String> allergens,
        List<String> ingredients
) {
}

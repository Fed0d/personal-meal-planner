package fedod.meal.service.dto;

import fedod.meal.service.entity.enums.MealType;

import java.math.BigDecimal;
import java.util.List;

public record DishFilterParams(
        MealType mealType,
        String cuisine,
        BigDecimal minCalories,
        BigDecimal maxCalories,
        List<String> excludeAllergens,
        String title
) {
}

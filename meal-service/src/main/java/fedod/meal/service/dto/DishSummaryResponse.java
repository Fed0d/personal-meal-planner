package fedod.meal.service.dto;

import fedod.meal.service.entity.enums.MealType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record DishSummaryResponse(
        Long id,
        String title,
        String dishImage,
        BigDecimal calories,
        BigDecimal protein,
        BigDecimal fat,
        BigDecimal carbs,
        String readyIn,
        String cuisine,
        MealType mealType,
        List<String> allergens
) {
}

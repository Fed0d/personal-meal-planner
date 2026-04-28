package fedod.meal.service.dto;

import fedod.meal.service.entity.enums.MealType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DishResponse(
        Long id,
        String url,
        String title,
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
        MealType mealType,
        List<String> allergens,
        List<String> ingredients,
        LocalDateTime createdAt
) {
}

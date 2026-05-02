package fedod.meal.plan.service.dto;

import fedod.meal.plan.service.entity.enums.MealSlot;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record MealPlanItemResponse(
        UUID id,
        MealSlot mealSlot,
        UUID dishId,
        String dishName,
        BigDecimal calories
) {
}

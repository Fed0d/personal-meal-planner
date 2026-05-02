package fedod.meal.plan.service.dto;

import fedod.meal.plan.service.entity.enums.MealSlot;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record MealPlanItemRequest(
        @NotNull MealSlot mealSlot,
        @NotNull UUID dishId,
        @NotBlank @Size(max = 200) String dishName,
        @NotNull @DecimalMin("0.0") BigDecimal calories
) {
}

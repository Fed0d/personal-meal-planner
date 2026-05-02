package fedod.meal.plan.orchestrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReplaceDishRequest(
        @NotNull UUID mealPlanId,
        @NotBlank String mealSlot,
        @NotNull UUID currentDishId
) {
}

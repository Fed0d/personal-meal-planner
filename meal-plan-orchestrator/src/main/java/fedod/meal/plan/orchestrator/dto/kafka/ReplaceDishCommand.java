package fedod.meal.plan.orchestrator.dto.kafka;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ReplaceDishCommand(
        UUID jobId,
        UUID userId,
        UUID mealPlanId,
        String mealSlot,
        UUID currentDishId
) {
}

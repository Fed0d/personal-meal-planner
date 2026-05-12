package fedod.meal.plan.orchestrator.dto.kafka;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GenerateMealPlanCommand(
        UUID jobId,
        UUID userId,
        String date
) {
}

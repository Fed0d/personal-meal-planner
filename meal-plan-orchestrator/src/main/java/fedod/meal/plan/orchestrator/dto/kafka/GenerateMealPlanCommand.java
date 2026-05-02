package fedod.meal.plan.orchestrator.dto.kafka;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record GenerateMealPlanCommand(
        UUID jobId,
        UUID userId,
        LocalDate date
) {
}

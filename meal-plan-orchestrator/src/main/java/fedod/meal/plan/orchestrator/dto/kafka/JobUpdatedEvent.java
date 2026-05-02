package fedod.meal.plan.orchestrator.dto.kafka;

import java.util.UUID;

public record JobUpdatedEvent(
        UUID jobId,
        String status,
        String message
) {
}

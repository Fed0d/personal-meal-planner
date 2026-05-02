package fedod.meal.plan.orchestrator.dto;

import fedod.meal.plan.orchestrator.entity.enums.JobStatus;
import fedod.meal.plan.orchestrator.entity.enums.JobType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record JobResponse(
        UUID id,
        UUID userId,
        JobType type,
        JobStatus status,
        String resultMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

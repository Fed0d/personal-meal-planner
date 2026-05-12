package fedod.meal.plan.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        String date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
}

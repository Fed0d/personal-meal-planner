package fedod.meal.plan.service.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record MealPlanResponse(
        UUID id,
        UUID userId,
        LocalDate date,
        List<MealPlanItemResponse> items,
        LocalDateTime createdAt
) {
}

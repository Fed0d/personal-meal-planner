package fedod.meal.plan.service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record MealPlanSummaryResponse(
        UUID id,
        LocalDate date,
        BigDecimal totalCalories
) {
}

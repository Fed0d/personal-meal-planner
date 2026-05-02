package fedod.meal.plan.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record CreateMealPlanRequest(
        @NotNull UUID userId,
        @NotNull LocalDate date,
        @NotNull @NotEmpty @Valid List<MealPlanItemRequest> items
) {
}

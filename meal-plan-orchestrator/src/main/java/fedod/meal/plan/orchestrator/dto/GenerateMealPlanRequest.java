package fedod.meal.plan.orchestrator.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GenerateMealPlanRequest(
        @NotNull @FutureOrPresent LocalDate date
) {
}

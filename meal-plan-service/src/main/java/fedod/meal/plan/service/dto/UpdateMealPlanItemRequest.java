package fedod.meal.plan.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateMealPlanItemRequest(
        @NotNull Long dishId,
        @NotBlank @Size(max = 200) String dishName,
        @NotNull @DecimalMin("0.0") BigDecimal calories
) {
}

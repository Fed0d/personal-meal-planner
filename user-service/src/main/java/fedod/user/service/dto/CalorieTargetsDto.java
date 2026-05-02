package fedod.user.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CalorieTargetsDto(
        BigDecimal bmr,
        BigDecimal targetCalories
) {
}

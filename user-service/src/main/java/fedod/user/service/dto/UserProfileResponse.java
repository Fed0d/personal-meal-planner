package fedod.user.service.dto;

import fedod.user.service.entity.enums.ActivityLevel;
import fedod.user.service.entity.enums.Gender;
import fedod.user.service.entity.enums.GoalType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserProfileResponse(
        UUID userId,
        String firstName,
        String lastName,
        LocalDate birthDate,
        Gender gender,
        Integer heightCm,
        BigDecimal weightKg,
        BigDecimal targetWeightKg,
        GoalType goalType,
        ActivityLevel activityLevel,
        BigDecimal bmr,
        BigDecimal targetCalories,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

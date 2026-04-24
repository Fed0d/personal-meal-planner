package fedod.user.service.dto;

import fedod.user.service.entity.enums.Gender;
import fedod.user.service.entity.enums.GoalType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpsertUserProfileRequest(
        @Size(max = 100, message = "First name must be at most 100 characters")
        String firstName,
        @Size(max = 100, message = "Last name must be at most 100 characters")
        String lastName,
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,
        Gender gender,
        @Min(value = 50, message = "Height must be at least 50 cm")
        @Max(value = 300, message = "Height must be at most 300 cm")
        Integer heightCm,
        @DecimalMin(value = "20.00", message = "Weight must be at least 20 kg")
        @DecimalMax(value = "500.00", message = "Weight must be at most 500 kg")
        BigDecimal weightKg,
        @DecimalMin(value = "20.00", message = "Target weight must be at least 20 kg")
        @DecimalMax(value = "500.00", message = "Target weight must be at most 500 kg")
        BigDecimal targetWeightKg,
        GoalType goalType
) {
}

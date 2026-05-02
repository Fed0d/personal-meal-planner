package fedod.user.service.service;

import fedod.user.service.entity.enums.ActivityLevel;
import fedod.user.service.entity.enums.Gender;
import fedod.user.service.entity.enums.GoalType;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface BmrCalculationService {

    BmrResult calculate(Gender gender, LocalDate birthDate, int heightCm,
                        BigDecimal weightKg, ActivityLevel activityLevel, GoalType goalType);
}

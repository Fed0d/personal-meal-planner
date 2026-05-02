package fedod.user.service.service.impl;

import fedod.user.service.entity.enums.ActivityLevel;
import fedod.user.service.entity.enums.Gender;
import fedod.user.service.entity.enums.GoalType;
import fedod.user.service.service.BmrCalculationService;
import fedod.user.service.service.BmrResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

@Service
public class BmrCalculationServiceImpl implements BmrCalculationService {

    private static final Map<ActivityLevel, Double> ACTIVITY_MULTIPLIERS = Map.of(
            ActivityLevel.SEDENTARY, 1.2,
            ActivityLevel.LOW, 1.375,
            ActivityLevel.MODERATE, 1.55,
            ActivityLevel.HIGH, 1.725,
            ActivityLevel.VERY_HIGH, 1.9
    );

    private static final Map<GoalType, Double> GOAL_MULTIPLIERS = Map.of(
            GoalType.LOSE_WEIGHT, 0.8,
            GoalType.MAINTAIN_WEIGHT, 1.0,
            GoalType.GAIN_WEIGHT, 1.15
    );

    @Override
    public BmrResult calculate(Gender gender, LocalDate birthDate, int heightCm,
                               BigDecimal weightKg, ActivityLevel activityLevel, GoalType goalType) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        double genderOffset = (gender == Gender.MALE) ? 5.0 : -161.0;

        double bmrRaw = 10.0 * weightKg.doubleValue() + 6.25 * heightCm - 5.0 * age + genderOffset;

        double tdee = bmrRaw * ACTIVITY_MULTIPLIERS.get(activityLevel);
        double targetCaloriesRaw = tdee * GOAL_MULTIPLIERS.get(goalType);

        BigDecimal bmr = BigDecimal.valueOf(bmrRaw).setScale(2, RoundingMode.HALF_UP);
        BigDecimal targetCalories = BigDecimal.valueOf(targetCaloriesRaw).setScale(2, RoundingMode.HALF_UP);

        return new BmrResult(bmr, targetCalories);
    }
}

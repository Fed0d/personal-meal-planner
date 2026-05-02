package fedod.user.service.service.impl;

import fedod.user.service.dto.CalorieTargetsDto;
import fedod.user.service.entity.UserCalorieTargets;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.UserCalorieTargetsRepository;
import fedod.user.service.service.CalorieTargetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CalorieTargetsServiceImpl implements CalorieTargetsService {

    private final UserCalorieTargetsRepository calorieTargetsRepository;

    @Override
    @Transactional(readOnly = true)
    public CalorieTargetsDto get(UUID userId) {
        UserCalorieTargets entity = calorieTargetsRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("Calorie targets not found for userId: " + userId));
        return CalorieTargetsDto.builder()
                .bmr(entity.getBmr())
                .targetCalories(entity.getTargetCalories())
                .build();
    }
}

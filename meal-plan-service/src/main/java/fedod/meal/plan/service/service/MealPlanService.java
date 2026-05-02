package fedod.meal.plan.service.service;

import fedod.meal.plan.service.dto.CreateMealPlanRequest;
import fedod.meal.plan.service.dto.MealPlanResponse;
import fedod.meal.plan.service.dto.MealPlanSummaryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MealPlanService {

    MealPlanResponse create(CreateMealPlanRequest request);

    MealPlanResponse getById(UUID id, UUID requestingUserId);

    MealPlanResponse getByUserAndDate(UUID userId, LocalDate date);

    List<MealPlanSummaryResponse> getAllByUser(UUID userId);
}

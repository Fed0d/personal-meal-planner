package fedod.meal.plan.service.service.impl;

import fedod.meal.plan.service.dto.CreateMealPlanRequest;
import fedod.meal.plan.service.dto.MealPlanItemResponse;
import fedod.meal.plan.service.dto.MealPlanResponse;
import fedod.meal.plan.service.dto.MealPlanSummaryResponse;
import fedod.meal.plan.service.entity.MealPlan;
import fedod.meal.plan.service.entity.MealPlanItem;
import fedod.meal.plan.service.exception.MealPlanNotFoundException;
import fedod.meal.plan.service.repository.MealPlanRepository;
import fedod.meal.plan.service.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MealPlanServiceImpl implements MealPlanService {

    private final MealPlanRepository mealPlanRepository;

    @Override
    @Transactional
    public MealPlanResponse create(CreateMealPlanRequest request) {
        log.info("Creating meal plan for userId: {}, date: {}", request.userId(), request.date());

        MealPlan plan = new MealPlan();
        plan.setUserId(request.userId());
        plan.setDate(request.date());

        List<MealPlanItem> items = request.items().stream()
                .map(itemRequest -> {
                    MealPlanItem item = new MealPlanItem();
                    item.setMealPlan(plan);
                    item.setMealSlot(itemRequest.mealSlot());
                    item.setDishId(itemRequest.dishId());
                    item.setDishName(itemRequest.dishName());
                    item.setCalories(itemRequest.calories());
                    return item;
                })
                .toList();

        plan.getItems().addAll(items);
        MealPlan saved = mealPlanRepository.save(plan);

        log.info("Meal plan created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MealPlanResponse getById(UUID id, UUID requestingUserId) {
        MealPlan plan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new MealPlanNotFoundException("Meal plan not found: " + id));

        if (!plan.getUserId().equals(requestingUserId)) {
            throw new AccessDeniedException("Access denied to meal plan: " + id);
        }

        return toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public MealPlanResponse getByUserAndDate(UUID userId, LocalDate date) {
        MealPlan plan = mealPlanRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new MealPlanNotFoundException(
                        "Meal plan not found for userId: " + userId + " and date: " + date));
        return toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealPlanSummaryResponse> getAllByUser(UUID userId) {
        return mealPlanRepository.findByUserId(userId).stream()
                .map(plan -> {
                    BigDecimal totalCalories = plan.getItems().stream()
                            .map(MealPlanItem::getCalories)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return MealPlanSummaryResponse.builder()
                            .id(plan.getId())
                            .date(plan.getDate())
                            .totalCalories(totalCalories)
                            .build();
                })
                .toList();
    }

    private MealPlanResponse toResponse(MealPlan plan) {
        List<MealPlanItemResponse> itemResponses = plan.getItems().stream()
                .map(item -> MealPlanItemResponse.builder()
                        .id(item.getId())
                        .mealSlot(item.getMealSlot())
                        .dishId(item.getDishId())
                        .dishName(item.getDishName())
                        .calories(item.getCalories())
                        .build())
                .toList();

        return MealPlanResponse.builder()
                .id(plan.getId())
                .userId(plan.getUserId())
                .date(plan.getDate())
                .items(itemResponses)
                .createdAt(plan.getCreatedAt())
                .build();
    }
}

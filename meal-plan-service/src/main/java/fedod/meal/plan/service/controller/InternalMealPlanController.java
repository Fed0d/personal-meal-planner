package fedod.meal.plan.service.controller;

import fedod.meal.plan.service.dto.MealPlanItemResponse;
import fedod.meal.plan.service.dto.UpdateMealPlanItemRequest;
import fedod.meal.plan.service.entity.enums.MealSlot;
import fedod.meal.plan.service.service.MealPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/meal-plans")
@RequiredArgsConstructor
public class InternalMealPlanController {

    private final MealPlanService mealPlanService;

    @GetMapping("/by-user/{userId}/dish-ids")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('SERVICE')")
    public List<Long> getDishIdsByUser(@PathVariable UUID userId) {
        log.info("Internal: getting dish IDs for userId: {}", userId);
        return mealPlanService.getDishIdsByUser(userId);
    }

    @PatchMapping("/{mealPlanId}/items/{slot}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('SERVICE')")
    public MealPlanItemResponse replaceItem(
            @PathVariable UUID mealPlanId,
            @PathVariable MealSlot slot,
            @Valid @RequestBody UpdateMealPlanItemRequest request
    ) {
        log.info("Internal: replacing slot {} in meal plan {}", slot, mealPlanId);
        return mealPlanService.replaceItem(mealPlanId, slot, request);
    }
}

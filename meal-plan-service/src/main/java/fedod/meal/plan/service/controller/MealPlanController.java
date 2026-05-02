package fedod.meal.plan.service.controller;

import fedod.meal.plan.service.dto.CreateMealPlanRequest;
import fedod.meal.plan.service.dto.MealPlanResponse;
import fedod.meal.plan.service.dto.MealPlanSummaryResponse;
import fedod.meal.plan.service.service.MealPlanService;
import fedod.security.jwt.model.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SERVICE')")
    public MealPlanResponse create(@Valid @RequestBody CreateMealPlanRequest request) {
        log.info("Received request to create meal plan for userId: {}", request.userId());
        return mealPlanService.create(request);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public List<MealPlanSummaryResponse> getMyPlans(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received request to list meal plans for userId: {}", principal.userId());
        return mealPlanService.getAllByUser(principal.userId());
    }

    @GetMapping("/my/date/{date}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public MealPlanResponse getMyPlanByDate(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("Received request to get meal plan for userId: {}, date: {}", principal.userId(), date);
        return mealPlanService.getByUserAndDate(principal.userId(), date);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public MealPlanResponse getById(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable UUID id
    ) {
        log.info("Received request to get meal plan {} for userId: {}", id, principal.userId());
        return mealPlanService.getById(id, principal.userId());
    }
}

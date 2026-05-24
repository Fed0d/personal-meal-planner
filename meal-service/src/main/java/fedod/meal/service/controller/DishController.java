package fedod.meal.service.controller;

import fedod.meal.service.dto.CreateDishRequest;
import fedod.meal.service.dto.DishFilterParams;
import fedod.meal.service.dto.DishResponse;
import fedod.meal.service.dto.DishSummaryResponse;
import fedod.meal.service.entity.enums.MealType;
import fedod.meal.service.service.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SERVICE')")
    public Page<DishSummaryResponse> getDishes(
            @RequestParam(required = false) MealType mealType,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) BigDecimal minCalories,
            @RequestParam(required = false) BigDecimal maxCalories,
            @RequestParam(required = false) List<String> excludeAllergens,
            @RequestParam(required = false) String title,
            @PageableDefault(size = 20, sort = "title") Pageable pageable
    ) {
        DishFilterParams params = new DishFilterParams(mealType, cuisine, minCalories, maxCalories, excludeAllergens, title);
        return dishService.getDishes(params, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'SERVICE')")
    public DishResponse getDishById(@PathVariable Long id) {
        return dishService.getDishById(id);
    }

    /**
     * Creates a dish in the catalog.
     * <p>
     * Allowed for:
     * <ul>
     *     <li>USER role — typically administrative imports (curated food.ru data);</li>
     *     <li>SERVICE role — external services authenticated via service API key
     *     (in particular, the Python ML service publishes here LLM-generated recipes
     *     with {@code aiGenerated=true} and {@code id=null} so the backend allocates
     *     a unique negative id).</li>
     * </ul>
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER', 'SERVICE')")
    public DishResponse createDish(@Valid @RequestBody CreateDishRequest request) {
        return dishService.createDish(request);
    }
}

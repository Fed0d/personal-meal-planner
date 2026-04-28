package fedod.meal.service.service.impl;

import fedod.meal.service.dto.CreateDishRequest;
import fedod.meal.service.dto.DishFilterParams;
import fedod.meal.service.dto.DishResponse;
import fedod.meal.service.dto.DishSummaryResponse;
import fedod.meal.service.entity.Dish;
import fedod.meal.service.entity.DishAllergen;
import fedod.meal.service.entity.DishIngredient;
import fedod.meal.service.exception.DishNotFoundException;
import fedod.meal.service.repository.DishRepository;
import fedod.meal.service.repository.specification.DishSpecification;
import fedod.meal.service.service.DishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<DishSummaryResponse> getDishes(DishFilterParams params, Pageable pageable) {
        log.debug("Fetching dishes with filters: {}", params);
        Specification<Dish> spec = DishSpecification.withFilters(params);
        return dishRepository.findAll(spec, pageable).map(this::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DishResponse getDishById(Long id) {
        log.debug("Fetching dish by id: {}", id);
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException(id));
        return toResponse(dish);
    }

    @Override
    @Transactional
    public DishResponse createDish(CreateDishRequest request) {
        log.debug("Creating dish with id: {}", request.id());
        Dish dish = Dish.builder()
                .id(request.id())
                .url(request.url())
                .title(request.title())
                .description(request.description())
                .dishImage(request.dishImage())
                .calories(request.calories())
                .protein(request.protein())
                .fat(request.fat())
                .carbs(request.carbs())
                .readyIn(request.readyIn())
                .kitchenTime(request.kitchenTime())
                .cuisine(request.cuisine())
                .categoryPath(request.categoryPath())
                .recipe(request.recipe())
                .mealType(request.mealType())
                .build();

        if (request.allergens() != null) {
            request.allergens().forEach(a ->
                    dish.getAllergens().add(DishAllergen.builder().dish(dish).allergen(a).build())
            );
        }

        if (request.ingredients() != null) {
            request.ingredients().forEach(i ->
                    dish.getIngredients().add(DishIngredient.builder().dish(dish).ingredient(i).build())
            );
        }

        Dish saved = dishRepository.save(dish);
        log.info("Created dish id={} title={}", saved.getId(), saved.getTitle());
        return toResponse(saved);
    }

    private DishSummaryResponse toSummaryResponse(Dish dish) {
        return DishSummaryResponse.builder()
                .id(dish.getId())
                .title(dish.getTitle())
                .dishImage(dish.getDishImage())
                .calories(dish.getCalories())
                .protein(dish.getProtein())
                .fat(dish.getFat())
                .carbs(dish.getCarbs())
                .readyIn(dish.getReadyIn())
                .cuisine(dish.getCuisine())
                .mealType(dish.getMealType())
                .allergens(dish.getAllergens().stream().map(DishAllergen::getAllergen).toList())
                .build();
    }

    private DishResponse toResponse(Dish dish) {
        return DishResponse.builder()
                .id(dish.getId())
                .url(dish.getUrl())
                .title(dish.getTitle())
                .description(dish.getDescription())
                .dishImage(dish.getDishImage())
                .calories(dish.getCalories())
                .protein(dish.getProtein())
                .fat(dish.getFat())
                .carbs(dish.getCarbs())
                .readyIn(dish.getReadyIn())
                .kitchenTime(dish.getKitchenTime())
                .cuisine(dish.getCuisine())
                .categoryPath(dish.getCategoryPath())
                .recipe(dish.getRecipe())
                .mealType(dish.getMealType())
                .allergens(dish.getAllergens().stream().map(DishAllergen::getAllergen).toList())
                .ingredients(dish.getIngredients().stream().map(DishIngredient::getIngredient).toList())
                .createdAt(dish.getCreatedAt())
                .build();
    }
}

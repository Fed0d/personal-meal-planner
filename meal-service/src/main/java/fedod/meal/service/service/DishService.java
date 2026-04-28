package fedod.meal.service.service;

import fedod.meal.service.dto.CreateDishRequest;
import fedod.meal.service.dto.DishFilterParams;
import fedod.meal.service.dto.DishResponse;
import fedod.meal.service.dto.DishSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DishService {

    Page<DishSummaryResponse> getDishes(DishFilterParams params, Pageable pageable);

    DishResponse getDishById(Long id);

    DishResponse createDish(CreateDishRequest request);
}

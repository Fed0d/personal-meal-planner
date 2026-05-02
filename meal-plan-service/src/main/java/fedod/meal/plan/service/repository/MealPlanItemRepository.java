package fedod.meal.plan.service.repository;

import fedod.meal.plan.service.entity.MealPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MealPlanItemRepository extends JpaRepository<MealPlanItem, UUID> {
}

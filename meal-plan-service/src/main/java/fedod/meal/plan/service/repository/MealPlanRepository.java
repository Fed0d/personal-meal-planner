package fedod.meal.plan.service.repository;

import fedod.meal.plan.service.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MealPlanRepository extends JpaRepository<MealPlan, UUID> {

    List<MealPlan> findByUserId(UUID userId);

    Optional<MealPlan> findByUserIdAndDate(UUID userId, LocalDate date);
}

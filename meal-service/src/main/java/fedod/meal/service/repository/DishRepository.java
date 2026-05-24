package fedod.meal.service.repository;

import fedod.meal.service.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {

    /**
     * Minimum existing id in the dish table — used to allocate the next negative id
     * for AI-generated dishes (id space below 0 is reserved for them).
     */
    @Query("select min(d.id) from Dish d")
    Optional<Long> findMinId();
}

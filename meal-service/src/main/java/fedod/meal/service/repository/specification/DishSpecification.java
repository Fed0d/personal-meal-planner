package fedod.meal.service.repository.specification;

import fedod.meal.service.dto.DishFilterParams;
import fedod.meal.service.entity.Dish;
import fedod.meal.service.entity.DishAllergen;
import fedod.meal.service.entity.enums.MealType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public class DishSpecification {

    private DishSpecification() {
    }

    public static Specification<Dish> withFilters(DishFilterParams params) {
        return Specification
                .where(hasMealType(params.mealType()))
                .and(hasCuisine(params.cuisine()))
                .and(minCalories(params.minCalories()))
                .and(maxCalories(params.maxCalories()))
                .and(titleContains(params.title()))
                .and(excludeAllergens(params.excludeAllergens()));
    }

    private static Specification<Dish> hasMealType(MealType mealType) {
        return (root, query, cb) ->
                mealType == null ? null : cb.equal(root.get("mealType"), mealType);
    }

    private static Specification<Dish> hasCuisine(String cuisine) {
        return (root, query, cb) ->
                cuisine == null ? null : cb.equal(root.get("cuisine"), cuisine);
    }

    private static Specification<Dish> minCalories(BigDecimal minCalories) {
        return (root, query, cb) ->
                minCalories == null ? null : cb.greaterThanOrEqualTo(root.get("calories"), minCalories);
    }

    private static Specification<Dish> maxCalories(BigDecimal maxCalories) {
        return (root, query, cb) ->
                maxCalories == null ? null : cb.lessThanOrEqualTo(root.get("calories"), maxCalories);
    }

    private static Specification<Dish> titleContains(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    private static Specification<Dish> excludeAllergens(List<String> excludeAllergens) {
        if (excludeAllergens == null || excludeAllergens.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<DishAllergen> allergenRoot = subquery.from(DishAllergen.class);
            subquery.select(allergenRoot.get("dish").get("id"))
                    .where(allergenRoot.get("allergen").in(excludeAllergens));
            return cb.not(root.get("id").in(subquery));
        };
    }
}

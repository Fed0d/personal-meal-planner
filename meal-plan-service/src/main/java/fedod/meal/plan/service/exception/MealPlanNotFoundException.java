package fedod.meal.plan.service.exception;

public class MealPlanNotFoundException extends RuntimeException {
    public MealPlanNotFoundException(String message) {
        super(message);
    }
}

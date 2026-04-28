package fedod.meal.service.exception;

public class DishNotFoundException extends RuntimeException {

    public DishNotFoundException(Long id) {
        super("Dish not found with id: " + id);
    }
}

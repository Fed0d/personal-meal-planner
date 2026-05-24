package fedod.meal.plan.orchestrator.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record IngredientChatRequest(
        @NotEmpty(message = "Список ингредиентов не может быть пустым")
        List<String> ingredients
) {
}

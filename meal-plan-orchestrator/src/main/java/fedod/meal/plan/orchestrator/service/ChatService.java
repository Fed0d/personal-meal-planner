package fedod.meal.plan.orchestrator.service;

import fedod.meal.plan.orchestrator.dto.IngredientChatRequest;
import fedod.meal.plan.orchestrator.dto.advisor.MealAdvisorResponse;

import java.util.UUID;

public interface ChatService {

    MealAdvisorResponse fromIngredients(UUID userId, IngredientChatRequest request);
}

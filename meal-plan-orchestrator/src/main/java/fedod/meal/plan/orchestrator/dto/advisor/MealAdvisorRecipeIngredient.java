package fedod.meal.plan.orchestrator.dto.advisor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MealAdvisorRecipeIngredient(String item, String amount) {}

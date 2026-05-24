package fedod.meal.plan.orchestrator.dto.advisor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MealAdvisorResponse(
        @JsonProperty("available_ingredients") List<String> availableIngredients,
        @JsonProperty("gap_report") MealAdvisorGapReport gapReport,
        List<MealAdvisorRecipe> recipes
) {}

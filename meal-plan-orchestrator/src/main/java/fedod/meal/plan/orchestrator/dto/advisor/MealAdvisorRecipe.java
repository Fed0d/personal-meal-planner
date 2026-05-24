package fedod.meal.plan.orchestrator.dto.advisor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MealAdvisorRecipe(
        String name,
        String style,
        String description,
        List<MealAdvisorRecipeIngredient> ingredients,
        List<String> instructions,
        @JsonProperty("preparation_time") String preparationTime,
        @JsonProperty("nutritional_highlights") List<String> nutritionalHighlights,
        @JsonProperty("gaps_addressed") List<String> gapsAddressed,
        @JsonProperty("computed_balance_score") Integer computedBalanceScore
) {}

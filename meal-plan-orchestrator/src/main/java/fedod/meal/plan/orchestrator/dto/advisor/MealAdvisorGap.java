package fedod.meal.plan.orchestrator.dto.advisor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MealAdvisorGap(
        String nutrient,
        double current,
        double target,
        String unit,
        @JsonProperty("percent_met") double percentMet,
        String severity
) {}

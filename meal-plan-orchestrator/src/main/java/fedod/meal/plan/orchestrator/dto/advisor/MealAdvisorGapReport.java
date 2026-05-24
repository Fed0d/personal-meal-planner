package fedod.meal.plan.orchestrator.dto.advisor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MealAdvisorGapReport(
        @JsonProperty("balance_score") int balanceScore,
        List<String> strengths,
        List<String> weaknesses,
        @JsonProperty("candidate_additions") List<String> candidateAdditions,
        List<MealAdvisorGap> gaps
) {}

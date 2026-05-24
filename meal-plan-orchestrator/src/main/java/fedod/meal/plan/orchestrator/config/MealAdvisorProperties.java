package fedod.meal.plan.orchestrator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "meal-advisor")
public class MealAdvisorProperties {
    private String url = "http://meal-advisor:8000";
    private int connectTimeoutMs = 5_000;
    private int readTimeoutMs = 90_000;
}

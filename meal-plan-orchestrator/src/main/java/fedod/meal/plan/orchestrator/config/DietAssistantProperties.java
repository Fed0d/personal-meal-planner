package fedod.meal.plan.orchestrator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "diet-assistant")
public class DietAssistantProperties {
    private String url = "http://diet-assistant:8080";
    private int connectTimeoutMs = 5_000;
    private int readTimeoutMs = 30_000;
}

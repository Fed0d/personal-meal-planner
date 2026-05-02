package fedod.meal.plan.orchestrator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicsProperties {

    private String generateMealplanCommand;
    private String replaceMealplanCommand;
    private String jobUpdatedEvent;
}

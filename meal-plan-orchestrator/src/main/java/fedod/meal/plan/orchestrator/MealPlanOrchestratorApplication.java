package fedod.meal.plan.orchestrator;

import fedod.meal.plan.orchestrator.config.KafkaTopicsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class MealPlanOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MealPlanOrchestratorApplication.class, args);
    }
}

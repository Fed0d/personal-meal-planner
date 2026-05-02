package fedod.meal.plan.orchestrator.kafka;

import fedod.meal.plan.orchestrator.config.KafkaTopicsProperties;
import fedod.meal.plan.orchestrator.dto.kafka.GenerateMealPlanCommand;
import fedod.meal.plan.orchestrator.dto.kafka.ReplaceDishCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MealPlanCommandPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public void publishGenerateMealPlan(GenerateMealPlanCommand command) {
        kafkaTemplate.send(topics.getGenerateMealplanCommand(), command.jobId().toString(), command);
        log.info("Published GenerateMealPlanCommand for jobId: {}", command.jobId());
    }

    public void publishReplaceDish(ReplaceDishCommand command) {
        kafkaTemplate.send(topics.getReplaceMealplanCommand(), command.jobId().toString(), command);
        log.info("Published ReplaceDishCommand for jobId: {}", command.jobId());
    }
}

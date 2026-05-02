package fedod.meal.plan.orchestrator.kafka;


import fedod.meal.plan.orchestrator.config.KafkaTopicsProperties;
import fedod.meal.plan.orchestrator.dto.kafka.GenerateMealPlanCommand;
import fedod.meal.plan.orchestrator.dto.kafka.ReplaceDishCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class MealPlanCommandPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicsProperties topics;
    private final ObjectMapper objectMapper;

    public void publishGenerateMealPlan(GenerateMealPlanCommand command) {
        String payload = serialize(command);
        kafkaTemplate.send(topics.getGenerateMealplanCommand(), command.jobId().toString(), payload);
        log.info("Published GenerateMealPlanCommand for jobId: {}", command.jobId());
    }

    public void publishReplaceDish(ReplaceDishCommand command) {
        String payload = serialize(command);
        kafkaTemplate.send(topics.getReplaceMealplanCommand(), command.jobId().toString(), payload);
        log.info("Published ReplaceDishCommand for jobId: {}", command.jobId());
    }

    private String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Kafka message", e);
        }
    }
}

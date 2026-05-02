package fedod.meal.plan.orchestrator.kafka;

import fedod.meal.plan.orchestrator.dto.kafka.JobUpdatedEvent;
import fedod.meal.plan.orchestrator.service.OrchestratorJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobUpdatedEventConsumer {

    private final OrchestratorJobService orchestratorJobService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.job-updated-event}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            JobUpdatedEvent event = objectMapper.readValue(message, JobUpdatedEvent.class);
            log.info("Received JobUpdatedEvent for jobId: {}, status: {}", event.jobId(), event.status());
            orchestratorJobService.handleJobUpdated(event);
        } catch (Exception e) {
            log.error("Failed to process JobUpdatedEvent: {}", message, e);
        }
    }
}

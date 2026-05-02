package fedod.meal.plan.orchestrator.kafka;

import fedod.meal.plan.orchestrator.dto.kafka.JobUpdatedEvent;
import fedod.meal.plan.orchestrator.service.OrchestratorJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobUpdatedEventConsumer {

    private final OrchestratorJobService orchestratorJobService;

    @KafkaListener(topics = "${kafka.topics.job-updated-event}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(JobUpdatedEvent event) {
        log.info("Received JobUpdatedEvent for jobId: {}, status: {}", event.jobId(), event.status());
        orchestratorJobService.handleJobUpdated(event);
    }
}

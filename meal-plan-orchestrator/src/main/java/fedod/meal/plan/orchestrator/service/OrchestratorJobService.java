package fedod.meal.plan.orchestrator.service;

import fedod.meal.plan.orchestrator.dto.GenerateMealPlanRequest;
import fedod.meal.plan.orchestrator.dto.JobResponse;
import fedod.meal.plan.orchestrator.dto.ReplaceDishRequest;
import fedod.meal.plan.orchestrator.dto.kafka.JobUpdatedEvent;

import java.util.List;
import java.util.UUID;

public interface OrchestratorJobService {

    JobResponse requestGenerate(UUID userId, GenerateMealPlanRequest request);

    JobResponse requestReplace(UUID userId, ReplaceDishRequest request);

    List<JobResponse> getUserJobs(UUID userId);

    JobResponse getJobById(UUID jobId, UUID requestingUserId);

    void handleJobUpdated(JobUpdatedEvent event);
}

package fedod.meal.plan.orchestrator.service.impl;

import fedod.meal.plan.orchestrator.dto.GenerateMealPlanRequest;
import fedod.meal.plan.orchestrator.dto.JobResponse;
import fedod.meal.plan.orchestrator.dto.ReplaceDishRequest;
import fedod.meal.plan.orchestrator.dto.kafka.GenerateMealPlanCommand;
import fedod.meal.plan.orchestrator.dto.kafka.JobUpdatedEvent;
import fedod.meal.plan.orchestrator.dto.kafka.ReplaceDishCommand;
import fedod.meal.plan.orchestrator.entity.Job;
import fedod.meal.plan.orchestrator.entity.enums.JobStatus;
import fedod.meal.plan.orchestrator.entity.enums.JobType;
import fedod.meal.plan.orchestrator.exception.JobNotFoundException;
import fedod.meal.plan.orchestrator.kafka.MealPlanCommandPublisher;
import fedod.meal.plan.orchestrator.repository.JobRepository;
import fedod.meal.plan.orchestrator.service.OrchestratorJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrchestratorJobServiceImpl implements OrchestratorJobService {

    private final JobRepository jobRepository;
    private final MealPlanCommandPublisher commandPublisher;

    @Override
    @Transactional
    public JobResponse requestGenerate(UUID userId, GenerateMealPlanRequest request) {
        Job job = createJob(userId, JobType.GENERATE_MEAL_PLAN);

        commandPublisher.publishGenerateMealPlan(GenerateMealPlanCommand.builder()
                .jobId(job.getId())
                .userId(userId)
                .date(request.date())
                .build());

        log.info("Generate meal plan job created: jobId={}, userId={}, date={}", job.getId(), userId, request.date());
        return toResponse(job);
    }

    @Override
    @Transactional
    public JobResponse requestReplace(UUID userId, ReplaceDishRequest request) {
        Job job = createJob(userId, JobType.REPLACE_DISH);

        commandPublisher.publishReplaceDish(ReplaceDishCommand.builder()
                .jobId(job.getId())
                .userId(userId)
                .mealPlanId(request.mealPlanId())
                .mealSlot(request.mealSlot())
                .currentDishId(request.currentDishId())
                .build());

        log.info("Replace dish job created: jobId={}, userId={}, mealPlanId={}", job.getId(), userId, request.mealPlanId());
        return toResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getUserJobs(UUID userId) {
        return jobRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID jobId, UUID requestingUserId) {
        Job job = findById(jobId);
        if (!job.getUserId().equals(requestingUserId)) {
            throw new AccessDeniedException("Access denied to job: " + jobId);
        }
        return toResponse(job);
    }

    @Override
    @Transactional
    public void handleJobUpdated(JobUpdatedEvent event) {
        Job job = findById(event.jobId());
        job.setStatus(JobStatus.valueOf(event.status()));
        job.setResultMessage(event.message());
        jobRepository.save(job);
        log.info("Job {} updated to status: {}", event.jobId(), event.status());
    }

    private Job createJob(UUID userId, JobType type) {
        Job job = new Job();
        job.setUserId(userId);
        job.setType(type);
        job.setStatus(JobStatus.PENDING);
        return jobRepository.save(job);
    }

    private Job findById(UUID jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .userId(job.getUserId())
                .type(job.getType())
                .status(job.getStatus())
                .resultMessage(job.getResultMessage())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}

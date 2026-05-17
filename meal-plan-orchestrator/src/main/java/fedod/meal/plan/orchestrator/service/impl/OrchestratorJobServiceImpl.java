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
import fedod.meal.plan.orchestrator.mapper.JobMapper;
import fedod.meal.plan.orchestrator.repository.JobRepository;
import fedod.meal.plan.orchestrator.service.OrchestratorJobService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrchestratorJobServiceImpl implements OrchestratorJobService {

    private final JobRepository jobRepository;
    private final MealPlanCommandPublisher commandPublisher;
    private final JobMapper jobMapper;
    private final MeterRegistry meterRegistry;

    private Counter generateRequestsCounter;
    private Counter replaceRequestsCounter;
    private final Map<String, Counter> finishedCounters = new HashMap<>();

    @PostConstruct
    void initMetrics() {
        generateRequestsCounter = Counter.builder("orchestrator.jobs.requested")
                .tag("type", JobType.GENERATE_MEAL_PLAN.name())
                .description("Total meal plan generation requests")
                .register(meterRegistry);

        replaceRequestsCounter = Counter.builder("orchestrator.jobs.requested")
                .tag("type", JobType.REPLACE_DISH.name())
                .description("Total dish replacement requests")
                .register(meterRegistry);

        Gauge.builder("orchestrator.jobs.active", jobRepository,
                        repo -> repo.countByStatusIn(List.of(JobStatus.PENDING, JobStatus.IN_PROGRESS)))
                .description("Number of currently active jobs")
                .register(meterRegistry);

        for (JobType jobType : JobType.values()) {
            for (JobStatus terminalStatus : List.of(JobStatus.COMPLETED, JobStatus.FAILED)) {
                String key = jobType.name() + ":" + terminalStatus.name();
                finishedCounters.put(key, Counter.builder("orchestrator.jobs.finished")
                        .tag("type", jobType.name())
                        .tag("status", terminalStatus.name())
                        .description("Total completed jobs by type and outcome")
                        .register(meterRegistry));
                Timer.builder("orchestrator.job.processing.seconds")
                        .tag("type", jobType.name())
                        .tag("status", terminalStatus.name())
                        .description("Job processing time from creation to completion")
                        .register(meterRegistry);
            }
        }
    }

    @Override
    @Transactional
    public JobResponse requestGenerate(UUID userId, GenerateMealPlanRequest request) {
        Job job = createJob(userId, JobType.GENERATE_MEAL_PLAN);
        job.setDate(request.date().toString());
        jobRepository.save(job);

        commandPublisher.publishGenerateMealPlan(GenerateMealPlanCommand.builder()
                .jobId(job.getId())
                .userId(userId)
                .date(request.date().toString())
                .build());

        generateRequestsCounter.increment();
        log.info("Generate meal plan job created: jobId={}, userId={}, date={}", job.getId(), userId, request.date());
        return jobMapper.toResponse(job);
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

        replaceRequestsCounter.increment();
        log.info("Replace dish job created: jobId={}, userId={}, mealPlanId={}", job.getId(), userId, request.mealPlanId());
        return jobMapper.toResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getUserJobs(UUID userId) {
        return jobRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(jobMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID jobId, UUID requestingUserId) {
        Job job = findById(jobId);
        if (!job.getUserId().equals(requestingUserId)) {
            throw new AccessDeniedException("Access denied to job: " + jobId);
        }
        return jobMapper.toResponse(job);
    }

    @Override
    @Transactional
    public void handleJobUpdated(JobUpdatedEvent event) {
        Job job = findById(event.jobId());
        job.setStatus(JobStatus.valueOf(event.status()));
        job.setResultMessage(event.message());
        jobRepository.save(job);

        String type = job.getType().name();
        String status = event.status();

        boolean isTerminal = JobStatus.COMPLETED.name().equals(status) || JobStatus.FAILED.name().equals(status);
        if (isTerminal) {
            Counter finished = finishedCounters.get(type + ":" + status);
            if (finished != null) {
                finished.increment();
            }

            if (job.getCreatedAt() != null) {
                Duration duration = Duration.between(job.getCreatedAt(), LocalDateTime.now());
                Timer.builder("orchestrator.job.processing.seconds")
                        .tag("type", type)
                        .tag("status", status)
                        .description("Job processing time from creation to completion")
                        .register(meterRegistry)
                        .record(duration);
            }
        }

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
}

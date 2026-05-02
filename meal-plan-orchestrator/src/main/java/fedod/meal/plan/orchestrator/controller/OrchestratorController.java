package fedod.meal.plan.orchestrator.controller;

import fedod.meal.plan.orchestrator.dto.GenerateMealPlanRequest;
import fedod.meal.plan.orchestrator.dto.JobResponse;
import fedod.meal.plan.orchestrator.dto.ReplaceDishRequest;
import fedod.meal.plan.orchestrator.service.OrchestratorJobService;
import fedod.security.jwt.model.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/orchestrator")
@RequiredArgsConstructor
public class OrchestratorController {

    private final OrchestratorJobService orchestratorJobService;

    @PostMapping("/plans/generate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('USER')")
    public JobResponse requestGenerate(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody GenerateMealPlanRequest request
    ) {
        log.info("Received generate meal plan request from userId: {}", principal.userId());
        return orchestratorJobService.requestGenerate(principal.userId(), request);
    }

    @PostMapping("/plans/replace")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('USER')")
    public JobResponse requestReplace(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody ReplaceDishRequest request
    ) {
        log.info("Received replace dish request from userId: {}", principal.userId());
        return orchestratorJobService.requestReplace(principal.userId(), request);
    }

    @GetMapping("/tasks")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public List<JobResponse> getMyJobs(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received get jobs request for userId: {}", principal.userId());
        return orchestratorJobService.getUserJobs(principal.userId());
    }

    @GetMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public JobResponse getJobById(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable UUID id
    ) {
        log.info("Received get job {} request for userId: {}", id, principal.userId());
        return orchestratorJobService.getJobById(id, principal.userId());
    }
}

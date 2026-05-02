package fedod.meal.plan.orchestrator.repository;

import fedod.meal.plan.orchestrator.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

package fedod.user.service.repository;

import fedod.user.service.entity.UserBodyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserBodyMetricsRepository extends JpaRepository<UserBodyMetrics, UUID> {
}

package fedod.user.service.repository;

import fedod.user.service.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserGoalRepository extends JpaRepository<UserGoal, UUID> {
}

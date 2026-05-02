package fedod.user.service.repository;

import fedod.user.service.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserActivityRepository extends JpaRepository<UserActivity, UUID> {
}

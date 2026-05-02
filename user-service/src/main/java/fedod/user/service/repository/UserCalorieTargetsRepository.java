package fedod.user.service.repository;

import fedod.user.service.entity.UserCalorieTargets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCalorieTargetsRepository extends JpaRepository<UserCalorieTargets, UUID> {
}

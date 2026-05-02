package fedod.user.service.repository;

import fedod.user.service.entity.UserCookingPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCookingPreferencesRepository extends JpaRepository<UserCookingPreferences, UUID> {
}

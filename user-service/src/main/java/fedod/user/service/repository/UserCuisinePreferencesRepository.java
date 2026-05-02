package fedod.user.service.repository;

import fedod.user.service.entity.UserCuisinePreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCuisinePreferencesRepository extends JpaRepository<UserCuisinePreferences, UUID> {
}

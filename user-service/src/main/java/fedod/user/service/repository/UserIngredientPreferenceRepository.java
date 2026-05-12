package fedod.user.service.repository;

import fedod.user.service.entity.UserIngredientPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserIngredientPreferenceRepository extends JpaRepository<UserIngredientPreference, UUID> {
}

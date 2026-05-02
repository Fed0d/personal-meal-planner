package fedod.user.service.repository;

import fedod.user.service.entity.UserAllergens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserAllergensRepository extends JpaRepository<UserAllergens, UUID> {
}

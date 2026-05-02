package fedod.user.service.repository;

import fedod.user.service.entity.UserIngredientPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserIngredientPreferenceRepository extends JpaRepository<UserIngredientPreference, Long> {

    List<UserIngredientPreference> findByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM UserIngredientPreference u WHERE u.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}

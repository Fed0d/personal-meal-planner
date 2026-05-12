package fedod.user.service.repository;

import fedod.user.service.entity.UserDishReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDishReactionRepository extends JpaRepository<UserDishReaction, Long> {

    List<UserDishReaction> findByUserId(UUID userId);

    Optional<UserDishReaction> findByUserIdAndDishId(UUID userId, Long dishId);

    @Modifying
    @Query("DELETE FROM UserDishReaction r WHERE r.userId = :userId AND r.dishId = :dishId")
    void deleteByUserIdAndDishId(@Param("userId") UUID userId, @Param("dishId") Long dishId);
}

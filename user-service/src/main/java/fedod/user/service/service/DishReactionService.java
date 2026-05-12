package fedod.user.service.service;

import fedod.user.service.dto.DishReactionDto;
import fedod.user.service.entity.enums.ReactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DishReactionService {

    List<DishReactionDto> getAll(UUID userId);

    Optional<ReactionType> getOne(UUID userId, Long dishId);

    DishReactionDto set(UUID userId, Long dishId, ReactionType reaction);

    void remove(UUID userId, Long dishId);
}

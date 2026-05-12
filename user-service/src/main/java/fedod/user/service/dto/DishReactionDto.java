package fedod.user.service.dto;

import fedod.user.service.entity.enums.ReactionType;
import lombok.Builder;

@Builder
public record DishReactionDto(Long dishId, ReactionType reaction) {
}

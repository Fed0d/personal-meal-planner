package fedod.user.service.dto;

import fedod.user.service.entity.enums.ReactionType;
import jakarta.validation.constraints.NotNull;

public record SetDishReactionRequest(@NotNull ReactionType reaction) {
}

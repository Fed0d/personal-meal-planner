package fedod.user.service.service.impl;

import fedod.user.service.dto.DishReactionDto;
import fedod.user.service.entity.UserDishReaction;
import fedod.user.service.entity.enums.ReactionType;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.UserDishReactionRepository;
import fedod.user.service.repository.UserProfileRepository;
import fedod.user.service.service.DishReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DishReactionServiceImpl implements DishReactionService {

    private final UserDishReactionRepository reactionRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DishReactionDto> getAll(UUID userId) {
        return reactionRepository.findByUserId(userId).stream()
                .map(r -> DishReactionDto.builder()
                        .dishId(r.getDishId())
                        .reaction(r.getReaction())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReactionType> getOne(UUID userId, Long dishId) {
        return reactionRepository.findByUserIdAndDishId(userId, dishId)
                .map(UserDishReaction::getReaction);
    }

    @Override
    @Transactional
    public DishReactionDto set(UUID userId, Long dishId, ReactionType reaction) {
        if (!userProfileRepository.existsById(userId)) {
            throw new UserProfileNotFoundException("User profile not found for userId: " + userId);
        }
        UserDishReaction entity = reactionRepository.findByUserIdAndDishId(userId, dishId)
                .orElseGet(() -> UserDishReaction.builder().userId(userId).dishId(dishId).build());
        entity.setReaction(reaction);
        reactionRepository.save(entity);
        log.info("Set reaction {} for dish {} by user {}", reaction, dishId, userId);
        return DishReactionDto.builder().dishId(dishId).reaction(reaction).build();
    }

    @Override
    @Transactional
    public void remove(UUID userId, Long dishId) {
        reactionRepository.deleteByUserIdAndDishId(userId, dishId);
        log.info("Removed reaction for dish {} by user {}", dishId, userId);
    }
}

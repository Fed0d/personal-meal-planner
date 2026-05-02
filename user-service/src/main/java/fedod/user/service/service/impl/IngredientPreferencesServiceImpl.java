package fedod.user.service.service.impl;

import fedod.user.service.dto.IngredientPreferenceDto;
import fedod.user.service.entity.UserIngredientPreference;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.UserIngredientPreferenceRepository;
import fedod.user.service.repository.UserProfileRepository;
import fedod.user.service.service.IngredientPreferencesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class IngredientPreferencesServiceImpl implements IngredientPreferencesService {

    private final UserProfileRepository userProfileRepository;
    private final UserIngredientPreferenceRepository ingredientPreferenceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IngredientPreferenceDto> get(UUID userId) {
        return ingredientPreferenceRepository.findByUserId(userId).stream()
                .map(e -> IngredientPreferenceDto.builder()
                        .name(e.getIngredientName())
                        .score(e.getScore())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public List<IngredientPreferenceDto> update(UUID userId, List<IngredientPreferenceDto> request) {
        if (!userProfileRepository.existsById(userId)) {
            throw new UserProfileNotFoundException("User profile not found for userId: " + userId);
        }

        ingredientPreferenceRepository.deleteByUserId(userId);

        List<UserIngredientPreference> entities = request.stream()
                .map(dto -> UserIngredientPreference.builder()
                        .userId(userId)
                        .ingredientName(dto.name())
                        .score(dto.score())
                        .build())
                .toList();
        ingredientPreferenceRepository.saveAll(entities);

        log.info("Ingredient preferences updated for userId: {}", userId);
        return request;
    }
}

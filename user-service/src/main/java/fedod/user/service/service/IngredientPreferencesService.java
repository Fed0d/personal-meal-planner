package fedod.user.service.service;

import fedod.user.service.dto.IngredientPreferenceDto;

import java.util.UUID;

public interface IngredientPreferencesService {

    IngredientPreferenceDto get(UUID userId);

    IngredientPreferenceDto update(UUID userId, IngredientPreferenceDto request);
}

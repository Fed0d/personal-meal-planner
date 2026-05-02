package fedod.user.service.service;

import fedod.user.service.dto.IngredientPreferenceDto;

import java.util.List;
import java.util.UUID;

public interface IngredientPreferencesService {

    List<IngredientPreferenceDto> get(UUID userId);

    List<IngredientPreferenceDto> update(UUID userId, List<IngredientPreferenceDto> request);
}

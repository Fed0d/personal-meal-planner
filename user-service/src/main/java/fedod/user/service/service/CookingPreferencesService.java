package fedod.user.service.service;

import fedod.user.service.dto.CookingPreferencesDto;

import java.util.UUID;

public interface CookingPreferencesService {

    CookingPreferencesDto get(UUID userId);

    CookingPreferencesDto update(UUID userId, CookingPreferencesDto request);
}

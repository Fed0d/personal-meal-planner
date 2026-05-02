package fedod.user.service.service;

import fedod.user.service.dto.CuisinePreferencesDto;

import java.util.UUID;

public interface CuisinePreferencesService {

    CuisinePreferencesDto get(UUID userId);

    CuisinePreferencesDto update(UUID userId, CuisinePreferencesDto request);
}

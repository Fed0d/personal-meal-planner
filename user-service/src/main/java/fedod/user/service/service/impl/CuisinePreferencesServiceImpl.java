package fedod.user.service.service.impl;

import fedod.user.service.dto.CuisinePreferencesDto;
import fedod.user.service.entity.UserCuisinePreferences;
import fedod.user.service.entity.UserProfile;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.UserCuisinePreferencesRepository;
import fedod.user.service.repository.UserProfileRepository;
import fedod.user.service.service.CuisinePreferencesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CuisinePreferencesServiceImpl implements CuisinePreferencesService {

    private final UserProfileRepository userProfileRepository;
    private final UserCuisinePreferencesRepository cuisinePreferencesRepository;

    @Override
    @Transactional(readOnly = true)
    public CuisinePreferencesDto get(UUID userId) {
        UserCuisinePreferences entity = cuisinePreferencesRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("Cuisine preferences not found for userId: " + userId));
        return toDto(entity);
    }

    @Override
    @Transactional
    public CuisinePreferencesDto update(UUID userId, CuisinePreferencesDto request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

        UserCuisinePreferences entity = profile.getCuisinePreferences();
        if (entity == null) {
            entity = new UserCuisinePreferences();
            profile.setCuisinePreferences(entity);
        }
        entity.setAsian(request.asian());
        entity.setEuropean(request.european());
        entity.setEastern(request.eastern());
        entity.setSlavic(request.slavic());
        entity.setAmerican(request.american());
        entity.setMexican(request.mexican());

        userProfileRepository.save(profile);
        log.info("Cuisine preferences updated for userId: {}", userId);
        return toDto(entity);
    }

    private CuisinePreferencesDto toDto(UserCuisinePreferences e) {
        return CuisinePreferencesDto.builder()
                .asian(e.getAsian())
                .european(e.getEuropean())
                .eastern(e.getEastern())
                .slavic(e.getSlavic())
                .american(e.getAmerican())
                .mexican(e.getMexican())
                .build();
    }
}

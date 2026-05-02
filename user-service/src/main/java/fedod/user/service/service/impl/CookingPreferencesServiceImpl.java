package fedod.user.service.service.impl;

import fedod.user.service.dto.CookingPreferencesDto;
import fedod.user.service.entity.UserCookingPreferences;
import fedod.user.service.entity.UserProfile;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.UserCookingPreferencesRepository;
import fedod.user.service.repository.UserProfileRepository;
import fedod.user.service.service.CookingPreferencesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CookingPreferencesServiceImpl implements CookingPreferencesService {

    private final UserProfileRepository userProfileRepository;
    private final UserCookingPreferencesRepository cookingPreferencesRepository;

    @Override
    @Transactional(readOnly = true)
    public CookingPreferencesDto get(UUID userId) {
        UserCookingPreferences entity = cookingPreferencesRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("Cooking preferences not found for userId: " + userId));
        return toDto(entity);
    }

    @Override
    @Transactional
    public CookingPreferencesDto update(UUID userId, CookingPreferencesDto request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

        UserCookingPreferences entity = profile.getUserCookingPreferences();
        if (entity == null) {
            entity = new UserCookingPreferences();
            profile.setCookingPreferences(entity);
        }
        entity.setActiveCookingTimeMin(request.activeCookingTimeMin());
        entity.setPassiveCookingTimeMin(request.passiveCookingTimeMin());

        userProfileRepository.save(profile);
        log.info("Cooking preferences updated for userId: {}", userId);
        return toDto(entity);
    }

    private CookingPreferencesDto toDto(UserCookingPreferences e) {
        return CookingPreferencesDto.builder()
                .activeCookingTimeMin(e.getActiveCookingTimeMin())
                .passiveCookingTimeMin(e.getPassiveCookingTimeMin())
                .build();
    }
}

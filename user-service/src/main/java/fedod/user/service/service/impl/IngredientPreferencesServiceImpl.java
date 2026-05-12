package fedod.user.service.service.impl;

import fedod.user.service.dto.IngredientPreferenceDto;
import fedod.user.service.entity.UserIngredientPreference;
import fedod.user.service.entity.UserProfile;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.UserIngredientPreferenceRepository;
import fedod.user.service.repository.UserProfileRepository;
import fedod.user.service.service.IngredientPreferencesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class IngredientPreferencesServiceImpl implements IngredientPreferencesService {

    private final UserProfileRepository userProfileRepository;
    private final UserIngredientPreferenceRepository ingredientPreferenceRepository;

    @Override
    @Transactional(readOnly = true)
    public IngredientPreferenceDto get(UUID userId) {
        UserIngredientPreference entity = ingredientPreferenceRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("Ingredient preferences not found for userId: " + userId));
        return toDto(entity);
    }

    @Override
    @Transactional
    public IngredientPreferenceDto update(UUID userId, IngredientPreferenceDto request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

        UserIngredientPreference entity = profile.getIngredientPreferences();
        if (entity == null) {
            entity = new UserIngredientPreference();
            profile.setIngredientPreferences(entity);
        }
        applyDto(entity, request);

        userProfileRepository.save(profile);
        log.info("Ingredient preferences updated for userId: {}", userId);
        return toDto(entity);
    }

    private void applyDto(UserIngredientPreference e, IngredientPreferenceDto dto) {
        e.setFish(dto.fish());
        e.setSeafood(dto.seafood());
        e.setPork(dto.pork());
        e.setBeef(dto.beef());
        e.setChicken(dto.chicken());
        e.setCheese(dto.cheese());
        e.setPotato(dto.potato());
        e.setOnion(dto.onion());
        e.setGarlic(dto.garlic());
        e.setTomatoes(dto.tomatoes());
        e.setLiver(dto.liver());
        e.setMilk(dto.milk());
        e.setCottageCheese(dto.cottageCheese());
        e.setOlives(dto.olives());
        e.setCelery(dto.celery());
        e.setCilantro(dto.cilantro());
        e.setPumpkin(dto.pumpkin());
        e.setEggplant(dto.eggplant());
        e.setNuts(dto.nuts());
    }

    private IngredientPreferenceDto toDto(UserIngredientPreference e) {
        return IngredientPreferenceDto.builder()
                .fish(e.getFish())
                .seafood(e.getSeafood())
                .pork(e.getPork())
                .beef(e.getBeef())
                .chicken(e.getChicken())
                .cheese(e.getCheese())
                .potato(e.getPotato())
                .onion(e.getOnion())
                .garlic(e.getGarlic())
                .tomatoes(e.getTomatoes())
                .liver(e.getLiver())
                .milk(e.getMilk())
                .cottageCheese(e.getCottageCheese())
                .olives(e.getOlives())
                .celery(e.getCelery())
                .cilantro(e.getCilantro())
                .pumpkin(e.getPumpkin())
                .eggplant(e.getEggplant())
                .nuts(e.getNuts())
                .build();
    }
}

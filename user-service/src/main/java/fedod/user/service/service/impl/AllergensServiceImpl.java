package fedod.user.service.service.impl;

import fedod.user.service.dto.AllergensDto;
import fedod.user.service.entity.UserAllergens;
import fedod.user.service.entity.UserProfile;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.UserAllergensRepository;
import fedod.user.service.repository.UserProfileRepository;
import fedod.user.service.service.AllergensService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllergensServiceImpl implements AllergensService {

    private final UserProfileRepository userProfileRepository;
    private final UserAllergensRepository allergensRepository;

    @Override
    @Transactional(readOnly = true)
    public AllergensDto get(UUID userId) {
        UserAllergens entity = allergensRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("Allergens not found for userId: " + userId));
        return toDto(entity);
    }

    @Override
    @Transactional
    public AllergensDto update(UUID userId, AllergensDto request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

        UserAllergens entity = profile.getAllergens();
        if (entity == null) {
            entity = new UserAllergens();
            profile.setAllergens(entity);
        }
        entity.setNuts(request.nuts());
        entity.setSesame(request.sesame());
        entity.setPeanut(request.peanut());
        entity.setFish(request.fish());
        entity.setCrustaceans(request.crustaceans());
        entity.setMolluscs(request.molluscs());
        entity.setDairy(request.dairy());
        entity.setGluten(request.gluten());
        entity.setEgg(request.egg());
        entity.setCelery(request.celery());
        entity.setSoy(request.soy());
        entity.setFoodAdditives(request.foodAdditives());
        entity.setMustard(request.mustard());
        entity.setStrawberry(request.strawberry());
        entity.setNone(request.none());

        userProfileRepository.save(profile);
        log.info("Allergens updated for userId: {}", userId);
        return toDto(entity);
    }

    private AllergensDto toDto(UserAllergens e) {
        return AllergensDto.builder()
                .nuts(e.getNuts())
                .sesame(e.getSesame())
                .peanut(e.getPeanut())
                .fish(e.getFish())
                .crustaceans(e.getCrustaceans())
                .molluscs(e.getMolluscs())
                .dairy(e.getDairy())
                .gluten(e.getGluten())
                .egg(e.getEgg())
                .celery(e.getCelery())
                .soy(e.getSoy())
                .foodAdditives(e.getFoodAdditives())
                .mustard(e.getMustard())
                .strawberry(e.getStrawberry())
                .none(e.getNone())
                .build();
    }
}
